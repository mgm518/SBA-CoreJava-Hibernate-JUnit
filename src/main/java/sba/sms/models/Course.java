package sba.sms.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
 * Course is a POJO, configured as a persistent class that represents (or maps to) a table name
 * 'course' in the database. A Course object contains fields that represent course information and a
 * mapping of 'courses' that indicate an inverse or referencing side of the relationship. Implement
 * Lombok annotations to eliminate boilerplate code.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  int id;
  @NonNull
  @Column(nullable = false, length = 50)
  String name;
  @NonNull
  @Column(nullable = false, length = 50)
  String instructor;
  @ManyToMany(mappedBy = "courses", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  Set<Student> students = new HashSet<>();

  public void addStudent(Student student) {
    this.students.add(student);
    student.getCourses().add(this);
  }
}
