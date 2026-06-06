package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.auth.RegisterRequest;
import com.example.employeemanagement.dto.department.DepartmentDto;
import com.example.employeemanagement.dto.employee.EmployeeDto;
import com.example.employeemanagement.dto.jobtitle.JobTitleDto;
import com.example.employeemanagement.dto.leave.LeaveRequestDto;
import com.example.employeemanagement.exception.BadRequestException;
import com.example.employeemanagement.repository.UserRepository;
import com.example.employeemanagement.service.AuditService;
import com.example.employeemanagement.service.AuthService;
import com.example.employeemanagement.service.DepartmentService;
import com.example.employeemanagement.service.EmployeeService;
import com.example.employeemanagement.service.JobTitleService;
import com.example.employeemanagement.service.LeaveService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class PageController {

    private final AuthService authService;
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final JobTitleService jobTitleService;
    private final LeaveService leaveService;
    private final AuditService auditService;
    private final UserRepository userRepository;

    public PageController(AuthService authService, EmployeeService employeeService,
                          DepartmentService departmentService, JobTitleService jobTitleService,
                          LeaveService leaveService, AuditService auditService, UserRepository userRepository) {
        this.authService = authService;
        this.employeeService = employeeService;
        this.departmentService = departmentService;
        this.jobTitleService = jobTitleService;
        this.leaveService = leaveService;
        this.auditService = auditService;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String welcome() {
        return "welcome";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("registerRequest", new RegisterRequest());
        return "register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute RegisterRequest registerRequest, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "register";
        }
        try {
            authService.registerWebUser(registerRequest);
            redirectAttributes.addFlashAttribute("successMessage", "Account created. You can now sign in.");
            return "redirect:/login";
        } catch (BadRequestException ex) {
            bindingResult.reject("registration", ex.getMessage());
            return "register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Dashboard");
        model.addAttribute("employeeCount", employeeService.count());
        model.addAttribute("departmentCount", departmentService.count());
        model.addAttribute("jobTitleCount", jobTitleService.count());
        model.addAttribute("leaveCount", leaveService.count());
        model.addAttribute("audits", auditService.findRecent(6));
        return "pages/dashboard";
    }

    @GetMapping("/employees")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public String employees(Model model) {
        model.addAttribute("pageTitle", "Employees");
        model.addAttribute("employees", employeeService.findAll());
        return "pages/employees";
    }

    @GetMapping("/employees/new")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String newEmployee(Model model) {
        prepareEmployeeForm(model, new EmployeeDto(), "New Employee");
        return "pages/employee-form";
    }

    @GetMapping("/employees/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String editEmployee(@PathVariable Long id, Model model) {
        prepareEmployeeForm(model, employeeService.findById(id), "Edit Employee");
        return "pages/employee-form";
    }

    @PostMapping("/employees/save")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String saveEmployee(@Valid @ModelAttribute("employee") EmployeeDto employee,
                               BindingResult bindingResult, Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareEmployeeForm(model, employee, employee.getId() == null ? "New Employee" : "Edit Employee");
            return "pages/employee-form";
        }
        if (employee.getId() == null) {
            employeeService.createEmployee(employee);
        } else {
            employeeService.updateEmployee(employee.getId(), employee);
        }
        return success(redirectAttributes, "Employee saved successfully.", "/employees");
    }

    @PostMapping("/employees/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        employeeService.deleteEmployee(id);
        return success(redirectAttributes, "Employee deleted successfully.", "/employees");
    }

    @GetMapping("/departments")
    public String departments(Model model) {
        model.addAttribute("pageTitle", "Departments");
        model.addAttribute("departments", departmentService.findAll());
        return "pages/departments";
    }

    @GetMapping("/departments/new")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String newDepartment(Model model) {
        model.addAttribute("pageTitle", "New Department");
        model.addAttribute("department", new DepartmentDto());
        return "pages/department-form";
    }

    @GetMapping("/departments/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String editDepartment(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Edit Department");
        model.addAttribute("department", departmentService.findById(id));
        return "pages/department-form";
    }

    @PostMapping("/departments/save")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String saveDepartment(@Valid @ModelAttribute("department") DepartmentDto department,
                                 BindingResult bindingResult, Model model,
                                 RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", department.getId() == null ? "New Department" : "Edit Department");
            return "pages/department-form";
        }
        if (department.getId() == null) departmentService.createDepartment(department);
        else departmentService.updateDepartment(department.getId(), department);
        return success(redirectAttributes, "Department saved successfully.", "/departments");
    }

    @PostMapping("/departments/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String deleteDepartment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        departmentService.deleteDepartment(id);
        return success(redirectAttributes, "Department deleted successfully.", "/departments");
    }

    @GetMapping("/job-titles")
    public String jobTitles(Model model) {
        model.addAttribute("pageTitle", "Job Titles");
        model.addAttribute("jobTitles", jobTitleService.findAll());
        return "pages/job-titles";
    }

    @GetMapping("/job-titles/new")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String newJobTitle(Model model) {
        model.addAttribute("pageTitle", "New Job Title");
        model.addAttribute("jobTitle", new JobTitleDto());
        return "pages/job-title-form";
    }

    @GetMapping("/job-titles/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String editJobTitle(@PathVariable Long id, Model model) {
        model.addAttribute("pageTitle", "Edit Job Title");
        model.addAttribute("jobTitle", jobTitleService.findById(id));
        return "pages/job-title-form";
    }

    @PostMapping("/job-titles/save")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String saveJobTitle(@Valid @ModelAttribute("jobTitle") JobTitleDto jobTitle,
                               BindingResult bindingResult, Model model,
                               RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", jobTitle.getId() == null ? "New Job Title" : "Edit Job Title");
            return "pages/job-title-form";
        }
        if (jobTitle.getId() == null) jobTitleService.createJobTitle(jobTitle);
        else jobTitleService.updateJobTitle(jobTitle.getId(), jobTitle);
        return success(redirectAttributes, "Job title saved successfully.", "/job-titles");
    }

    @PostMapping("/job-titles/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String deleteJobTitle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        jobTitleService.deleteJobTitle(id);
        return success(redirectAttributes, "Job title deleted successfully.", "/job-titles");
    }

    @GetMapping("/leaves")
    public String leaves(Model model) {
        model.addAttribute("pageTitle", "Leave Requests");
        model.addAttribute("leaves", leaveService.findAll());
        return "pages/leaves";
    }

    @GetMapping("/leaves/new")
    public String newLeave(Model model) {
        prepareLeaveForm(model, new LeaveRequestDto(), "New Leave Request");
        return "pages/leave-form";
    }

    @GetMapping("/leaves/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String editLeave(@PathVariable Long id, Model model) {
        prepareLeaveForm(model, leaveService.findById(id), "Edit Leave Request");
        return "pages/leave-form";
    }

    @PostMapping("/leaves/save")
    public String saveLeave(@Valid @ModelAttribute("leave") LeaveRequestDto leave,
                            BindingResult bindingResult, Model model,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            prepareLeaveForm(model, leave, leave.getId() == null ? "New Leave Request" : "Edit Leave Request");
            return "pages/leave-form";
        }
        try {
            if (leave.getId() == null) leaveService.createLeaveRequest(leave);
            else leaveService.updateLeaveRequest(leave.getId(), leave);
        } catch (BadRequestException ex) {
            bindingResult.reject("leave", ex.getMessage());
            prepareLeaveForm(model, leave, leave.getId() == null ? "New Leave Request" : "Edit Leave Request");
            return "pages/leave-form";
        }
        return success(redirectAttributes, "Leave request saved successfully.", "/leaves");
    }

    @PostMapping("/leaves/{id}/delete")
    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    public String deleteLeave(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        leaveService.deleteLeaveRequest(id);
        return success(redirectAttributes, "Leave request deleted successfully.", "/leaves");
    }

    @PostMapping("/leaves/{id}/approve")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public String approveLeave(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        leaveService.approveLeave(id, authentication.getName());
        return success(redirectAttributes, "Leave request approved.", "/leaves");
    }

    @PostMapping("/leaves/{id}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','HR','MANAGER')")
    public String rejectLeave(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        leaveService.rejectLeave(id, authentication.getName());
        return success(redirectAttributes, "Leave request rejected.", "/leaves");
    }

    @GetMapping("/audit-logs")
    @PreAuthorize("hasRole('ADMIN')")
    public String auditLogs(Model model) {
        model.addAttribute("pageTitle", "Audit Logs");
        model.addAttribute("audits", auditService.findAll());
        return "pages/audit-logs";
    }

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        model.addAttribute("pageTitle", "My Profile");
        model.addAttribute("user", userRepository.findByUsername(authentication.getName()).orElseThrow());
        return "pages/profile";
    }

    private void prepareEmployeeForm(Model model, EmployeeDto employee, String title) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("employee", employee);
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("departments", departmentService.findAll());
        model.addAttribute("jobTitles", jobTitleService.findAll());
        model.addAttribute("managers", employeeService.findAll());
    }

    private void prepareLeaveForm(Model model, LeaveRequestDto leave, String title) {
        model.addAttribute("pageTitle", title);
        model.addAttribute("leave", leave);
        model.addAttribute("employees", employeeService.findAll());
    }

    private String success(RedirectAttributes redirectAttributes, String message, String path) {
        redirectAttributes.addFlashAttribute("successMessage", message);
        return "redirect:" + path;
    }
}
