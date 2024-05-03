package sba.sms.services;

import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.Transaction;
import sba.sms.dao.StudentI;
import sba.sms.models.Course;
import sba.sms.models.Student;
import sba.sms.utils.HibernateUtil;

/**
 * StudentService is a concrete class. This class implements the StudentI interface, overrides all
 * abstract service methods and provides implementation for each method. Lombok @Log used to
 * generate a logger file.
 */
@Log
public class StudentService implements StudentI {

  private static final CourseService courseService = new CourseService();

  @Override
  public List<Student> getAllStudents() {
    List<Student> students = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      TypedQuery<Student> query = session.createQuery("FROM Student", Student.class);
      students = query.getResultList();
    } catch (Exception e) {
      log.throwing(this.getClass().getName(), "getAllStudents", e);
    }
    return students;
  }

  @Override
  public void createStudent(Student student) {
    Transaction tx = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      tx = session.beginTransaction();
      Optional.ofNullable(session.get(Student.class, student.getEmail()))
          .ifPresentOrElse(s -> { // Student exists in database
            /* Note: The presumption is to update the student's properties,
            though it would be better to have an `updateStudent` method for that. */
            s.setName(student.getName());
            s.setPassword(student.getPassword());
            session.merge(s);
          }, () -> { // Persist new Student
            session.persist(student);
          });
      tx.commit();
    } catch (Exception e) {
      Optional.ofNullable(tx).ifPresent(Transaction::rollback);
      log.throwing(this.getClass().getName(), "getAllStudents", e);
    }
  }

  @Override
  public Student getStudentByEmail(String email) {
    Student student = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      String hql = "FROM Student s WHERE s.email = :email";
      TypedQuery<Student> query = session.createQuery(hql, Student.class);
      query.setParameter("email", email);
      student = query.getSingleResult();
    } catch (Exception e) {
      log.throwing(this.getClass().getName(), "getStudentByEmail", e);
    }
    return student;
  }

  @Override
  public boolean validateStudent(String email, String password) {
    return Optional.ofNullable(getStudentByEmail(email))
        .map(student -> student.getPassword().equals(password)).orElse(false);
  }

  @Override
  public void registerStudentToCourse(String email, int courseId) {
    Transaction tx = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      tx = session.beginTransaction();
      Optional.ofNullable(getStudentByEmail(email))
          .ifPresent(student ->
              Optional.ofNullable(courseService.getCourseById(courseId)).ifPresent(course -> {
                student.addCourse(course);
                session.merge(student);
              }));
      tx.commit();
    } catch (Exception e) {
      Optional.ofNullable(tx).ifPresent(Transaction::rollback);
      log.throwing(this.getClass().getName(), "registerStudentToCourse", e);
    }
  }

  @Override
  public List<Course> getStudentCourses(String email) {
    return Optional.ofNullable(getStudentByEmail(email))
        .map(student -> List.copyOf(student.getCourses()))
        .orElse(List.of());
  }

}
