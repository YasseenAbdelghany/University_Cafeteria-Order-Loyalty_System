package Interfaces;

import Core.LoyaltyProgram;

public interface ILoyaltyProgram {
    public int save(LoyaltyProgram prog);
    public  void update(LoyaltyProgram prog);
    public LoyaltyProgram findById(int id);
    public void delete(int id);

}
