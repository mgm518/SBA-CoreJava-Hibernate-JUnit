package sba.sms.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import sba.sms.models.Student;
import sba.sms.utils.CommandLine;
import sba.sms.utils.HibernateUtil;


class StudentServiceTest {

  private static SessionFactory sessionFactory;
  private Session session;
  private Transaction transaction;
  private StudentService studentService;
  private CourseService courseService;

  private static final String PASSWORD = "password";

  @BeforeAll
  static void setup() {
    sessionFactory = HibernateUtil.getSessionFactory();
    CommandLine.addData();
  }

  @BeforeEach
  void init() {
    session = sessionFactory.openSession();
    transaction = session.beginTransaction();
    studentService = new StudentService();
    courseService = new CourseService();
  }

  @AfterEach
  void tearDown() {
    Optional.ofNullable(transaction).ifPresent(
        tx -> {
          if (tx.getStatus() != TransactionStatus.COMMITTED) {
            tx.rollback();
          }
        });
    Optional.ofNullable(session).ifPresent(Session::close);
  }

  @AfterAll
  static void shutdown() {
    HibernateUtil.shutdown();
  }

  @Test
  void testCreateStudent() {
    Student student = new Student("fames@hotmail.couk", "Elvis Lindsay", PASSWORD);
    studentService.createStudent(student);
    assertThat(studentService.getStudentByEmail("fames@hotmail.couk"))
        .isNotNull()
        .hasFieldOrPropertyWithValue("name", "Elvis Lindsay");
  }

  @Test
  void testGetAllStudents() {
    Student student = new Student("ante.dictum@google.edu", "Hasad Chang", PASSWORD);
    studentService.createStudent(student);
    assertThat(studentService.getAllStudents())
        .isNotNull().isNotEmpty().doesNotContainNull().contains(student);
  }

  @ParameterizedTest
  @ValueSource(strings = {"annette@gmail.com", "bolaji@gmail.com"})
  void testValidateStudentCorrectEmail(String email) {
    assertThat(studentService.validateStudent(email, PASSWORD)).isTrue();
    assertThat(studentService.validateStudent(email, PASSWORD + "incorrect")).isFalse();
  }

  @ParameterizedTest
  @ValueSource(strings = {"wrong@gmail.com", "false@gmail.com"})
  void testValidateStudentIncorrectEmail(String email) {
    assertThat(studentService.validateStudent(email, PASSWORD)).isFalse();
    assertThat(studentService.validateStudent(email, PASSWORD + "incorrect")).isFalse();
  }

  @Test
  void testRegisterStudentToCourse() {
    String email = "ariadna@gmail.com";
    studentService.registerStudentToCourse(email, 3);
    assertThat(studentService.getStudentByEmail(email).getCourses())
        .isNotNull()
        .isNotEmpty()
        .doesNotContainNull()
        .contains(courseService.getCourseById(3));
  }
}