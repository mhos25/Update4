/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Mahika
 */
public class LoopCount {
    
    private int loopNum;
    private int loopCount;
    
    public LoopCount (){
        this.loopCount = 0;
    }
    
    public void incrementLoopCount ()
    {
        ++ loopCount;
    }
    
    public void setLoopCount (int num){
        this.loopCount = num;
    }

    public int getloopNum(){
        loopNum = loopCount;
        return loopNum;
    }
}
