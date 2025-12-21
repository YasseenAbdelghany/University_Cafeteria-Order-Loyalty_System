package ServiceManager_int;



import ServiceManagers.StudentManagement;

import java.util.List;

public interface IStudentManagement  {

    boolean add(StudentManagement manager);
    boolean delete(String username);
    boolean update(String username, StudentManagement newManager);
    StudentManagement findByUsername(String username);
    List<StudentManagement> findAll();
    boolean exists(String username);
    void createDefaultManager();
    int count();

}
