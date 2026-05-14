package com.collegeapp.ui;

import com.collegeapp.model.Assignment;
import com.collegeapp.model.Attendance;
import com.collegeapp.model.Course;
import com.collegeapp.model.Department;
import com.collegeapp.model.Exam;
import com.collegeapp.model.Faculty;
import com.collegeapp.model.GradingScale;
import com.collegeapp.model.Mark;
import com.collegeapp.model.Student;
import com.collegeapp.model.Submission;
import com.collegeapp.model.University;
import com.collegeapp.model.User;
import com.collegeapp.service.AssignmentService;
import com.collegeapp.service.AttendanceService;
import com.collegeapp.service.AuthService;
import com.collegeapp.service.CourseService;
import com.collegeapp.service.DepartmentService;
import com.collegeapp.service.EnrollmentService;
import com.collegeapp.service.ExamService;
import com.collegeapp.service.FacultyService;
import com.collegeapp.service.GradingScaleService;
import com.collegeapp.service.MarkService;
import com.collegeapp.service.ServiceException;
import com.collegeapp.service.StudentService;
import com.collegeapp.service.UniversityService;
import com.collegeapp.util.PasswordUtil;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

public class MainFrame extends Application {

    private final StudentService studentService = new StudentService();
    private final FacultyService facultyService = new FacultyService();
    private final DepartmentService departmentService = new DepartmentService();
    private final CourseService courseService = new CourseService();
    private final EnrollmentService enrollmentService = new EnrollmentService();
    private final UniversityService universityService = new UniversityService();
    private final AttendanceService attendanceService = new AttendanceService();
    private final ExamService examService = new ExamService();
    private final MarkService markService = new MarkService();
    private final GradingScaleService gradingScaleService = new GradingScaleService();
    private final AssignmentService assignmentService = new AssignmentService();
    private final AuthService authService = new AuthService();

    private BorderPane root;
    private VBox sidebar;
    private StackPane content;
    private Label title;
    private Label subtitle;
    private Label status;
    private User currentUser;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        root = new BorderPane();
        root.getStyleClass().add("app-root");
        content = new StackPane();
        content.getStyleClass().add("content-area");
        sidebar = buildSidebar();
        root.setLeft(sidebar);
        root.setCenter(buildMainArea());

        Scene scene = new Scene(root, 1320, 820);
        Optional.ofNullable(getClass().getResource("/styles/campus.css"))
                .ifPresent(url -> scene.getStylesheets().add(url.toExternalForm()));
        stage.setMinWidth(1120);
        stage.setMinHeight(720);
        stage.setTitle("Campus Management System");
        stage.setScene(scene);
        stage.show();
        showDashboard();
    }

    private VBox buildSidebar() {
        VBox box = new VBox(10);
        box.getStyleClass().add("sidebar");
        Label brand = new Label("CMS");
        brand.getStyleClass().add("brand-mark");
        Label name = new Label("Campus Console");
        name.getStyleClass().add("brand-title");
        Label caption = new Label("Core Java Backend");
        caption.getStyleClass().add("brand-caption");
        box.getChildren().addAll(brand, name, caption, new Separator());
        addNav(box, "Overview", this::showDashboard);
        addNav(box, "Login", this::showLogin);
        addNav(box, "Students", this::showStudents);
        addNav(box, "Faculty", this::showFaculty);
        addNav(box, "Departments", this::showDepartments);
        addNav(box, "Courses", this::showCourses);
        addNav(box, "Enrollments", this::showEnrollments);
        addNav(box, "Academics", this::showAcademics);
        addNav(box, "Assignments", this::showAssignments);
        addNav(box, "University", this::showUniversity);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        Label user = new Label("Signed out");
        user.getStyleClass().add("sidebar-note");
        user.setId("sessionLabel");
        box.getChildren().addAll(spacer, user);
        return box;
    }

    private BorderPane buildMainArea() {
        BorderPane pane = new BorderPane();
        pane.getStyleClass().add("main-shell");
        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);
        VBox copy = new VBox(4);
        title = new Label();
        title.getStyleClass().add("page-title");
        subtitle = new Label();
        subtitle.getStyleClass().add("page-subtitle");
        copy.getChildren().addAll(title, subtitle);
        status = new Label("Ready");
        status.getStyleClass().add("status-pill");
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        header.getChildren().addAll(copy, spacer, status);
        header.getStyleClass().add("topbar");
        pane.setTop(header);
        pane.setCenter(content);
        return pane;
    }

    private void addNav(VBox box, String text, Runnable action) {
        Button button = new Button(text);
        button.getStyleClass().add("nav-button");
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(event -> action.run());
        box.getChildren().add(button);
    }

    private void setPage(String pageTitle, String pageSubtitle, Node node) {
        title.setText(pageTitle);
        subtitle.setText(pageSubtitle);
        content.getChildren().setAll(wrap(node));
    }

    private ScrollPane wrap(Node node) {
        ScrollPane scroll = new ScrollPane(node);
        scroll.setFitToWidth(true);
        scroll.getStyleClass().add("page-scroll");
        return scroll;
    }

    private void showDashboard() {
        VBox page = page();
        HBox cards = new HBox(16);
        cards.getChildren().addAll(
                metricCard("Students", "Live roster", () -> String.valueOf(studentService.listAll().size())),
                metricCard("Faculty", "Teaching staff", () -> String.valueOf(facultyService.listAll().size())),
                metricCard("Courses", "Academic catalog", () -> String.valueOf(courseService.listCourses().size())),
                metricCard("Departments", "University units", () -> String.valueOf(departmentService.listAll().size())));

        HBox modules = new HBox(16);
        modules.getChildren().addAll(
                featureCard("Academic Operations", "Enroll students, mark attendance, record exams, and publish marks."),
                featureCard("Assignment Workflow", "Create assignments, collect submissions, grade work, and discuss comments."),
                featureCard("Administration", "Manage tenants, departments, profiles, authentication, and user records."));

        page.getChildren().addAll(cards, modules, infoPanel(
                "Database-backed UI",
                "Every screen calls the service layer. If MySQL is unavailable, the UI stays open and reports the backend error in context."));
        setPage("Overview", "Operational dashboard for the campus backend", page);
    }

    private Node metricCard(String label, String caption, ThrowingSupplier<String> supplier) {
        VBox card = new VBox(8);
        card.getStyleClass().add("metric-card");
        Label value = new Label("...");
        value.getStyleClass().add("metric-value");
        Label name = new Label(label);
        name.getStyleClass().add("metric-label");
        Label sub = new Label(caption);
        sub.getStyleClass().add("muted");
        card.getChildren().addAll(value, name, sub);
        async(supplier, value::setText, error -> value.setText("Offline"));
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private Node featureCard(String label, String copy) {
        VBox card = new VBox(10);
        card.getStyleClass().add("feature-card");
        Label titleLabel = new Label(label);
        titleLabel.getStyleClass().add("section-title");
        Label body = new Label(copy);
        body.setWrapText(true);
        body.getStyleClass().add("muted");
        card.getChildren().addAll(titleLabel, body);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    private void showLogin() {
        VBox page = page();
        GridPane form = formGrid();
        TextField email = field("admin@college.edu");
        PasswordField password = new PasswordField();
        password.setPromptText("Password");
        form.addRow(0, label("Email"), email);
        form.addRow(1, label("Password"), password);
        Button login = primary("Sign in");
        Label result = new Label("Use credentials from your configured MySQL users table.");
        result.getStyleClass().add("muted");
        login.setOnAction(event -> {
            try {
                currentUser = authService.login(email.getText(), password.getText());
                result.setText("Signed in as " + currentUser.getDisplayName() + " (" + currentUser.getRole() + ")");
                updateSession();
                setStatus("Authenticated");
            } catch (ServiceException ex) {
                result.setText(ex.getMessage());
                setStatus("Login failed");
            }
        });
        page.getChildren().addAll(panel("Role Login", form, login, result));
        setPage("Login", "Authenticate against the backend user table", page);
    }

    private void showStudents() {
        TableView<Student> table = table();
        table.getColumns().addAll(
                col("ID", s -> s.getStudentId()),
                col("Roll No", Student::getRollNumber),
                col("Name", Student::getDisplayName),
                col("Email", Student::getEmail),
                col("Year", s -> s.getAcademicYear()),
                col("Sem", s -> s.getSem()),
                col("Dept", s -> s.getDeptId()));
        Button refresh = secondary("Refresh");
        refresh.setOnAction(e -> loadTable(table, () -> studentService.listAll()));
        Button add = primary("Add Student");
        add.setOnAction(e -> showStudentDialog().ifPresent(student -> runAction(
                () -> studentService.addStudent(student),
                "Student created",
                () -> loadTable(table, () -> studentService.listAll()))));
        loadTable(table, () -> studentService.listAll());
        setPage("Students", "Create and manage student records", module(table, actions(refresh, add)));
    }

    private void showFaculty() {
        TableView<Faculty> table = table();
        table.getColumns().addAll(
                col("ID", Faculty::getFacultyId),
                col("Employee", Faculty::getEmployeeId),
                col("Name", Faculty::getDisplayName),
                col("Email", Faculty::getEmail),
                col("Designation", Faculty::getDesignation),
                col("Dept", Faculty::getDeptId));
        Button refresh = secondary("Refresh");
        refresh.setOnAction(e -> loadTable(table, () -> facultyService.listAll()));
        Button add = primary("Add Faculty");
        add.setOnAction(e -> showFacultyDialog().ifPresent(faculty -> runAction(
                () -> facultyService.addFaculty(faculty),
                "Faculty created",
                () -> loadTable(table, () -> facultyService.listAll()))));
        loadTable(table, () -> facultyService.listAll());
        setPage("Faculty", "Manage teaching staff and department assignment", module(table, actions(refresh, add)));
    }

    private void showDepartments() {
        TableView<Department> table = table();
        table.getColumns().addAll(
                col("ID", Department::getDepartmentId),
                col("Code", Department::getDeptCode),
                col("Name", Department::getDeptName));
        Button refresh = secondary("Refresh");
        refresh.setOnAction(e -> loadTable(table, () -> departmentService.listAll()));
        Button add = primary("Add Department");
        add.setOnAction(e -> showDepartmentDialog().ifPresent(dept -> runAction(
                () -> departmentService.addDepartment(dept),
                "Department created",
                () -> loadTable(table, () -> departmentService.listAll()))));
        loadTable(table, () -> departmentService.listAll());
        setPage("Departments", "Maintain academic departments", module(table, actions(refresh, add)));
    }

    private void showCourses() {
        TableView<Course> table = table();
        table.getColumns().addAll(
                col("ID", Course::getCourseId),
                col("Code", Course::getCourseCode),
                col("Name", Course::getCourseName),
                col("Credits", Course::getCredits),
                col("Marks", Course::getTotalMarks),
                col("Hours", Course::getLectureHours),
                col("Sem", Course::getSemester),
                col("Dept", Course::getDeptId),
                col("Faculty", Course::getFacultyId));
        Button refresh = secondary("Refresh");
        refresh.setOnAction(e -> loadTable(table, () -> courseService.listCourses()));
        Button add = primary("Add Course");
        add.setOnAction(e -> showCourseDialog().ifPresent(course -> runAction(
                () -> courseService.addCourse(course),
                "Course created",
                () -> loadTable(table, () -> courseService.listCourses()))));
        loadTable(table, () -> courseService.listCourses());
        setPage("Courses", "Manage catalog, credits, semesters, and faculty assignment", module(table, actions(refresh, add)));
    }

    private void showEnrollments() {
        VBox page = page();
        GridPane form = formGrid();
        TextField studentId = field("1");
        TextField courseId = field("1");
        form.addRow(0, label("Student ID"), studentId);
        form.addRow(1, label("Course ID"), courseId);
        Button enroll = primary("Enroll Student");
        enroll.setOnAction(e -> runAction(
                () -> enrollmentService.enrollStudent(intValue(studentId), intValue(courseId)),
                "Student enrolled",
                null));
        Button drop = secondary("Drop Course");
        drop.setOnAction(e -> runAction(
                () -> {
                    enrollmentService.dropCourse(intValue(studentId), intValue(courseId));
                    return null;
                },
                "Course dropped",
                null));
        page.getChildren().addAll(panel("Enrollment Control", form, actions(enroll, drop)));
        setPage("Enrollments", "Enroll and drop students from courses", page);
    }

    private void showAcademics() {
        TabPane tabs = new TabPane();
        tabs.getStyleClass().add("tabs");
        tabs.getTabs().addAll(
                tab("Attendance", attendancePane()),
                tab("Exams", examsPane()),
                tab("Marks", marksPane()),
                tab("Grading Scale", gradingPane()));
        setPage("Academics", "Attendance, exams, marks, and grade configuration", tabs);
    }

    private Node attendancePane() {
        GridPane form = formGrid();
        TextField studentId = field("1");
        TextField courseId = field("1");
        TextField facultyId = field("1");
        DatePicker date = new DatePicker(LocalDate.now());
        ComboBox<Attendance.AttendanceStatus> statusBox = new ComboBox<>(
                FXCollections.observableArrayList(Attendance.AttendanceStatus.values()));
        statusBox.getSelectionModel().select(Attendance.AttendanceStatus.PRESENT);
        form.addRow(0, label("Student ID"), studentId);
        form.addRow(1, label("Course ID"), courseId);
        form.addRow(2, label("Faculty ID"), facultyId);
        form.addRow(3, label("Date"), date);
        form.addRow(4, label("Status"), statusBox);
        Button mark = primary("Mark Attendance");
        mark.setOnAction(e -> runAction(
                () -> attendanceService.markAttendance(new Attendance(0, intValue(studentId), intValue(courseId),
                        intValue(facultyId), date.getValue(), statusBox.getValue(), null)),
                "Attendance marked",
                null));
        return panel("Daily Attendance", form, mark);
    }

    private Node examsPane() {
        TableView<Exam> table = table();
        table.getColumns().addAll(
                col("ID", Exam::examId),
                col("Name", Exam::examName),
                col("Type", Exam::examType),
                col("Sem", Exam::semester),
                col("Year", Exam::academicYear),
                col("Admin", Exam::createdBy));
        Button refresh = secondary("Refresh");
        refresh.setOnAction(e -> loadTable(table, () -> examService.listExams()));
        Button add = primary("Create Exam");
        add.setOnAction(e -> showExamDialog().ifPresent(exam -> runAction(
                () -> examService.createExam(exam),
                "Exam created",
                () -> loadTable(table, () -> examService.listExams()))));
        loadTable(table, () -> examService.listExams());
        return module(table, actions(refresh, add));
    }

    private Node marksPane() {
        GridPane form = formGrid();
        TextField examId = field("1");
        TextField studentId = field("1");
        TextField courseId = field("1");
        TextField facultyId = field("1");
        TextField obtained = field("85");
        TextField total = field("100");
        form.addRow(0, label("Exam ID"), examId);
        form.addRow(1, label("Student ID"), studentId);
        form.addRow(2, label("Course ID"), courseId);
        form.addRow(3, label("Faculty ID"), facultyId);
        form.addRow(4, label("Marks Obtained"), obtained);
        form.addRow(5, label("Total Marks"), total);
        Button save = primary("Save Mark");
        save.setOnAction(e -> runAction(
                () -> {
                    markService.upsertMark(new Mark(0, intValue(examId), intValue(studentId), intValue(courseId),
                            intValue(facultyId), intValue(obtained), intValue(total), null));
                    return null;
                },
                "Mark saved",
                null));
        return panel("Marks Entry", form, save);
    }

    private Node gradingPane() {
        GridPane form = formGrid();
        TextField universityId = field("1");
        TextField min = field("90");
        TextField max = field("100");
        TextField grade = field("O");
        TextField points = field("10.0");
        form.addRow(0, label("University ID"), universityId);
        form.addRow(1, label("Min Marks"), min);
        form.addRow(2, label("Max Marks"), max);
        form.addRow(3, label("Grade"), grade);
        form.addRow(4, label("Points"), points);
        Button add = primary("Add Grade Band");
        add.setOnAction(e -> runAction(
                () -> gradingScaleService.addScale(new GradingScale(0, intValue(universityId), intValue(min),
                        intValue(max), grade.getText(), new BigDecimal(points.getText()))),
                "Grade band saved",
                null));
        return panel("Grading Scale", form, add);
    }

    private void showAssignments() {
        TabPane tabs = new TabPane();
        tabs.getTabs().addAll(tab("Assignments", assignmentsPane()), tab("Submissions", submissionsPane()));
        setPage("Assignments", "Create work, receive files, and grade submissions", tabs);
    }

    private Node assignmentsPane() {
        TableView<Assignment> table = table();
        table.getColumns().addAll(
                col("ID", Assignment::assignmentId),
                col("Title", Assignment::title),
                col("Course", Assignment::courseId),
                col("Faculty", Assignment::facultyId),
                col("Deadline", Assignment::deadline),
                col("Marks", Assignment::totalMarks));
        TextField courseId = field("1");
        Button refresh = secondary("Load Course");
        refresh.setOnAction(e -> loadTable(table, () -> assignmentService.listByCourse(intValue(courseId))));
        Button add = primary("Create Assignment");
        add.setOnAction(e -> showAssignmentDialog().ifPresent(assignment -> runAction(
                () -> assignmentService.createAssignment(assignment),
                "Assignment created",
                () -> loadTable(table, () -> assignmentService.listByCourse(assignment.courseId())))));
        return module(table, actions(label("Course ID"), courseId, refresh, add));
    }

    private Node submissionsPane() {
        GridPane form = formGrid();
        TextField assignmentId = field("1");
        TextField studentId = field("1");
        TextField path = field("uploads/assignment.pdf");
        TextArea comment = new TextArea();
        comment.setPromptText("Submission comment");
        comment.setPrefRowCount(4);
        form.addRow(0, label("Assignment ID"), assignmentId);
        form.addRow(1, label("Student ID"), studentId);
        form.addRow(2, label("File Path"), path);
        form.addRow(3, label("Comment"), comment);
        Button submit = primary("Submit");
        submit.setOnAction(e -> runAction(
                () -> assignmentService.submit(new Submission(0, intValue(assignmentId), intValue(studentId),
                        path.getText(), comment.getText(), null, null, null)),
                "Submission recorded",
                null));
        return panel("Student Submission", form, submit);
    }

    private void showUniversity() {
        TableView<University> table = table();
        table.getColumns().addAll(
                col("ID", University::universityId),
                col("Name", University::universityName),
                col("Domain", University::allowedDomain),
                col("Address", University::address));
        Button refresh = secondary("Refresh");
        refresh.setOnAction(e -> loadTable(table, () -> universityService.listUniversities()));
        Button add = primary("Add University");
        add.setOnAction(e -> showUniversityDialog().ifPresent(uni -> runAction(
                () -> universityService.addUniversity(uni),
                "University created",
                () -> loadTable(table, () -> universityService.listUniversities()))));
        loadTable(table, () -> universityService.listUniversities());
        setPage("University", "Manage tenant roots and allowed email domains", module(table, actions(refresh, add)));
    }

    private Optional<Student> showStudentDialog() {
        Dialog<Student> dialog = dialog("Add Student");
        GridPane form = formGrid();
        TextField username = field("student_001");
        TextField email = field("student@college.edu");
        PasswordField password = new PasswordField();
        password.setPromptText("Pass@1234");
        TextField roll = field("CS21IT001");
        TextField first = field("Pranav");
        TextField last = field("Dadhe");
        TextField mobile = field("9876543210");
        DatePicker dob = new DatePicker(LocalDate.of(2004, 1, 1));
        TextField year = field("2");
        TextField division = field("A");
        TextField batch = field("A1");
        TextField sem = field("3");
        TextField dept = field("1");
        addRows(form, label("Username"), username, label("Email"), email, label("Password"), password,
                label("Roll No"), roll, label("First Name"), first, label("Last Name"), last,
                label("Mobile"), mobile, label("DOB"), dob, label("Academic Year"), year,
                label("Division"), division, label("Batch"), batch, label("Semester"), sem, label("Dept ID"), dept);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Student(0, username.getText(), email.getText(), PasswordUtil.sha256(password.getText()),
                        "STUDENT", true, null, 0, roll.getText(), first.getText(), last.getText(), mobile.getText(),
                        dob.getValue(), intValue(year), division.getText(), batch.getText(), intValue(sem), intValue(dept))
                : null);
        return dialog.showAndWait();
    }

    private Optional<Faculty> showFacultyDialog() {
        Dialog<Faculty> dialog = dialog("Add Faculty");
        GridPane form = formGrid();
        TextField username = field("faculty_001");
        TextField email = field("faculty@college.edu");
        PasswordField password = new PasswordField();
        password.setPromptText("Pass@1234");
        TextField emp = field("EMP0001");
        TextField title = field("Dr.");
        TextField first = field("Asha");
        TextField last = field("Mehta");
        TextField designation = field("Assistant Professor");
        TextField mobile = field("9876543210");
        DatePicker dob = new DatePicker(LocalDate.of(1988, 1, 1));
        TextField dept = field("1");
        addRows(form, label("Username"), username, label("Email"), email, label("Password"), password,
                label("Employee ID"), emp, label("Title"), title, label("First Name"), first,
                label("Last Name"), last, label("Designation"), designation, label("Mobile"), mobile,
                label("DOB"), dob, label("Dept ID"), dept);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Faculty(0, username.getText(), email.getText(), PasswordUtil.sha256(password.getText()),
                        "FACULTY", true, null, 0, emp.getText(), title.getText(), first.getText(), last.getText(),
                        designation.getText(), mobile.getText(), dob.getValue(), intValue(dept))
                : null);
        return dialog.showAndWait();
    }

    private Optional<Department> showDepartmentDialog() {
        Dialog<Department> dialog = dialog("Add Department");
        GridPane form = formGrid();
        TextField name = field("Computer Science");
        TextField code = field("CSE");
        form.addRow(0, label("Department Name"), name);
        form.addRow(1, label("Department Code"), code);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK ? new Department(0, name.getText(), code.getText()) : null);
        return dialog.showAndWait();
    }

    private Optional<Course> showCourseDialog() {
        Dialog<Course> dialog = dialog("Add Course");
        GridPane form = formGrid();
        TextField code = field("CS301");
        TextField name = field("Data Structures");
        TextField credits = field("4");
        TextField marks = field("100");
        TextField hours = field("48");
        TextField semester = field("3");
        TextField dept = field("1");
        TextField faculty = field("1");
        addRows(form, label("Code"), code, label("Name"), name, label("Credits"), credits, label("Total Marks"), marks,
                label("Lecture Hours"), hours, label("Semester"), semester, label("Dept ID"), dept, label("Faculty ID"), faculty);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Course(0, code.getText(), name.getText(), intValue(credits), intValue(marks),
                        intValue(hours), intValue(semester), intValue(dept), intValue(faculty))
                : null);
        return dialog.showAndWait();
    }

    private Optional<Exam> showExamDialog() {
        Dialog<Exam> dialog = dialog("Create Exam");
        GridPane form = formGrid();
        TextField name = field("Mid Semester");
        TextField type = field("Internal");
        TextField sem = field("3");
        TextField year = field("2");
        TextField admin = field("1");
        addRows(form, label("Name"), name, label("Type"), type, label("Semester"), sem, label("Academic Year"), year, label("Admin ID"), admin);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Exam(0, name.getText(), type.getText(), intValue(sem), intValue(year), intValue(admin), null)
                : null);
        return dialog.showAndWait();
    }

    private Optional<Assignment> showAssignmentDialog() {
        Dialog<Assignment> dialog = dialog("Create Assignment");
        GridPane form = formGrid();
        TextField course = field("1");
        TextField faculty = field("1");
        TextField title = field("Unit 1 Practical");
        TextArea description = new TextArea();
        description.setPrefRowCount(3);
        TextField division = field("");
        TextField batch = field("");
        DatePicker dueDate = new DatePicker(LocalDate.now().plusDays(7));
        TextField marks = field("25");
        addRows(form, label("Course ID"), course, label("Faculty ID"), faculty, label("Title"), title,
                label("Description"), description, label("Division"), division, label("Batch"), batch,
                label("Deadline Date"), dueDate, label("Total Marks"), marks);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new Assignment(0, intValue(course), intValue(faculty), title.getText(), description.getText(),
                        blankToNull(division.getText()), blankToNull(batch.getText()),
                        dueDate.getValue().atTime(23, 59), intValue(marks), null)
                : null);
        return dialog.showAndWait();
    }

    private Optional<University> showUniversityDialog() {
        Dialog<University> dialog = dialog("Add University");
        GridPane form = formGrid();
        TextField name = field("DES Pune University");
        TextField domain = field("despu.edu.in");
        TextField logo = field("");
        TextArea address = new TextArea();
        address.setPrefRowCount(3);
        addRows(form, label("Name"), name, label("Allowed Domain"), domain, label("Logo Path"), logo, label("Address"), address);
        dialog.getDialogPane().setContent(form);
        dialog.setResultConverter(button -> button == ButtonType.OK
                ? new University(0, name.getText(), domain.getText(), blankToNull(logo.getText()), address.getText(), null)
                : null);
        return dialog.showAndWait();
    }

    private <T> void loadTable(TableView<T> table, ThrowingSupplier<List<T>> supplier) {
        setStatus("Loading");
        async(supplier, rows -> {
            ObservableList<T> data = FXCollections.observableArrayList(rows);
            table.setItems(data);
            setStatus(rows.size() + " rows loaded");
        }, error -> {
            table.setItems(FXCollections.observableArrayList());
            setStatus("Database unavailable");
            toast("Backend error", error.getMessage());
        });
    }

    private void runAction(ThrowingSupplier<?> action, String success, Runnable after) {
        try {
            action.get();
            setStatus(success);
            if (after != null) {
                after.run();
            }
        } catch (Exception ex) {
            setStatus("Action failed");
            toast("Action failed", ex.getMessage());
        }
    }

    private <T> void async(ThrowingSupplier<T> supplier, java.util.function.Consumer<T> success,
            java.util.function.Consumer<Exception> failure) {
        Task<T> task = new Task<>() {
            @Override
            protected T call() throws Exception {
                return supplier.get();
            }
        };
        task.setOnSucceeded(event -> success.accept(task.getValue()));
        task.setOnFailed(event -> failure.accept((Exception) task.getException()));
        Thread thread = new Thread(task, "cms-ui-task");
        thread.setDaemon(true);
        thread.start();
    }

    private VBox page() {
        VBox box = new VBox(18);
        box.setPadding(new Insets(24));
        return box;
    }

    private VBox module(Node body, Node controls) {
        VBox box = page();
        box.getChildren().addAll(controls, body);
        return box;
    }

    private HBox actions(Node... nodes) {
        HBox box = new HBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.getStyleClass().add("toolbar");
        box.getChildren().addAll(nodes);
        return box;
    }

    private VBox panel(String heading, Node... nodes) {
        VBox box = new VBox(14);
        box.getStyleClass().add("panel");
        Label label = new Label(heading);
        label.getStyleClass().add("section-title");
        box.getChildren().add(label);
        box.getChildren().addAll(nodes);
        return box;
    }

    private Node infoPanel(String heading, String text) {
        Label body = new Label(text);
        body.setWrapText(true);
        body.getStyleClass().add("muted");
        return panel(heading, body);
    }

    private GridPane formGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        ColumnConstraints labels = new ColumnConstraints();
        labels.setMinWidth(130);
        ColumnConstraints fields = new ColumnConstraints();
        fields.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(labels, fields);
        return grid;
    }

    private void addRows(GridPane grid, Node... nodes) {
        for (int i = 0, row = 0; i < nodes.length; i += 2, row++) {
            grid.addRow(row, nodes[i], nodes[i + 1]);
        }
    }

    private TextField field(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.setText(prompt);
        field.setMaxWidth(Double.MAX_VALUE);
        return field;
    }

    private Label label(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private Button primary(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("primary-button");
        return button;
    }

    private Button secondary(String text) {
        Button button = new Button(text);
        button.getStyleClass().add("secondary-button");
        return button;
    }

    private <T> TableView<T> table() {
        TableView<T> table = new TableView<>();
        table.getStyleClass().add("data-table");
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        table.setPrefHeight(540);
        return table;
    }

    private <T> TableColumn<T, String> col(String title, Function<T, Object> mapper) {
        TableColumn<T, String> column = new TableColumn<>(title);
        column.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(mapper.apply(data.getValue()))));
        return column;
    }

    private Tab tab(String title, Node node) {
        Tab tab = new Tab(title, node);
        tab.setClosable(false);
        return tab;
    }

    private <T> Dialog<T> dialog(String heading) {
        Dialog<T> dialog = new Dialog<>();
        dialog.setTitle(heading);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        Optional.ofNullable(getClass().getResource("/styles/campus.css"))
                .ifPresent(url -> dialog.getDialogPane().getStylesheets().add(url.toExternalForm()));
        return dialog;
    }

    private int intValue(TextField field) {
        return Integer.parseInt(field.getText().trim());
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }

    private void toast(String heading, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(heading);
            alert.setHeaderText(heading);
            alert.setContentText(message == null ? "Unknown error" : message);
            alert.showAndWait();
        });
    }

    private void setStatus(String text) {
        status.setText(text);
    }

    private void updateSession() {
        Node node = sidebar.lookup("#sessionLabel");
        if (node instanceof Label label) {
            label.setText(currentUser == null ? "Signed out" : currentUser.getDisplayName() + "\n" + currentUser.getRole());
        }
    }

    @FunctionalInterface
    private interface ThrowingSupplier<T> {
        T get() throws Exception;
    }
}
