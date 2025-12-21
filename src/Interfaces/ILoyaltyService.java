package Interfaces;

import Core.Student;
import Values.Money;
import Values.Discount;

public interface ILoyaltyService {
    void awardPoints(Student student, Money amount);
    Discount redeem(Student student, int points);
    int getBalance(Student student);
}
