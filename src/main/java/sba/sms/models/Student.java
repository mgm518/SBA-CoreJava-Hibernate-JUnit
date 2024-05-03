package sba.sms.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.HashSet;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;


/**
 * Student is a POJO, configured as a persistent class that represents (or maps to) a table name
 * 'student' in the database. A Student object contains fields that represent student login
 * credentials and a join table containing a registered student's email and course(s) data. The
 * Student class can be viewed as the owner of the bi-directional relationship. Implement Lombok
 * annotations to eliminate boilerplate code.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Student {

  @Id
  @NonNull
  @Column(nullable = false, length = 50, unique = true)
  String email;
  @NonNull
  @Column(nullable = false, length = 50)
  String name;
  @NonNull
  @Column(nullable = false, length = 50)
  String password;
  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(name = "student_courses",
      joinColumns = {@JoinColumn(name = "student_email")},
      inverseJoinColumns = {@JoinColumn(name = "courses_id")})
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  Set<Course> courses = new HashSet<>();

  public void addCourse(Course course) {
    this.courses.add(course);
    course.getStudents().add(this);
  }
}



