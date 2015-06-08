/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package indexmanager.utils;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author agustin
 */
public class Utils {
    
    
    
    public static long getTime(){
        //System.nano
        //Calendar cal = Calendar.getInstance();
        return System.nanoTime();
    }
    
    
    public static long substractTimes(long timeA,long timeB){
        //float time = timeA - timeB;
        return  timeA - timeB;
    }
    
    
    
    public static int getIntRepr(long number){
        int sign = (Float.floatToIntBits(number)>>>31);
        System.out.println(sign);
        System.out.println(Long.toBinaryString(number)); // Esta en complemento a 2.
        //if (number > Integer.MAX_VALUE){
        long complement = ~number;
        System.out.println(Long.toBinaryString(complement));
        if (number > Integer.MAX_VALUE){
            sign = 1;
        }
            // Lo paso a complemento a uno.
            // Retorno un int, signo en 1, y 31 bits complementados a 1
        //}
        return 0;
    }
    
}
