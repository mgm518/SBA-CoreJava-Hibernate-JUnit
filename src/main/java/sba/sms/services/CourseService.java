package sba.sms.services;

import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import lombok.extern.java.Log;
import org.hibernate.Session;
import org.hibernate.Transaction;
import sba.sms.dao.CourseI;
import sba.sms.models.Course;
import sba.sms.utils.HibernateUtil;

/**
 * CourseService is a concrete class. This class implements the CourseI interface, overrides all
 * abstract service methods and provides implementation for each method.
 */
@Log
public class CourseService implements CourseI {

  @Override
  public void createCourse(Course course) {
    Transaction tx = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      tx = session.beginTransaction();
      Optional.ofNullable(session.get(Course.class, course.getId()))
          .ifPresentOrElse(c -> { // If the course already exists.
            c.setName(course.getName());
            c.setInstructor(course.getInstructor());
            session.merge(c);
          }, () -> { // Persist new Course
            session.persist(course);
          });
      tx.commit();
    } catch (Exception e) {
      Optional.ofNullable(tx).ifPresent(Transaction::rollback);
      log.throwing(this.getClass().getName(), "createCourse", e);
    }
  }

  @Override
  public Course getCourseById(int courseId) {
    Course course = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      String hql = "from Course where id = :id";
      TypedQuery<Course> query = session.createQuery(hql, Course.class);
      query.setParameter("id", courseId);
      course = query.getSingleResult();
    } catch (Exception e) {
      log.throwing(this.getClass().getName(), "getCourseById", e);
    }
    return course;
  }

  @Override
  public List<Course> getAllCourses() {
    List<Course> courses = null;
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
      TypedQuery<Course> query = session.createQuery("from Course", Course.class);
      courses = query.getResultList();
    } catch (Exception e) {
      log.throwing(this.getClass().getName(), "getAllCourses", e);
    }
    return courses;
  }
}
