package Interfaces;

import Core.Student;

import java.util.List;

public interface IStudentRepository {

     boolean Save(Student student);
     Student FindByCode(String code);
     void Update(Student student);
     boolean deleteStudent(String code);
     List<Student> getAllStudents();
     int countStudents();
     Student authenticateStudent(String code, String password);

}
