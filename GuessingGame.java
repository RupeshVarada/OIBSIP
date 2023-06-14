import java.util.*;

public class GuessingGame {

    public static void main(String[] args) {
        Scanner sc=new Scanner(System.in);
        int Chances=5;
        int played=0;
        int Score=100;
        int number=(int)(Math.random()*99+1);
        System.out.println(" * * WELCOME TO NUMBER GUESSING GAME * *");
        System.out.println("RULES :");
        System.out.println("* ENTER THE NUMBER BETWEEN 1 TO 100..");
        System.out.println("* YOU HAVE 5 CHANCES TO GUESS THE CORRECT NUMBER..");
        System.out.println("* FOR EVERY WRONG GUESS 20 POINT WILL BE DEDUCTED..");
        while(true) {
        	System.out.print("\nENTER YOUR NUMBER : ");
        	int num=sc.nextInt();
        	if(num==number) 
        	{
        		System.out.println("HURRAY!! YOU WON THE GAME.\nYOUR SCORE IS "+Score);
        		break;
        	}
        	else if(num<number)
        	{
        		System.out.println("YOUR NUMBER IS SMALLER THAN ORIGINAL \nTRY ANOTHER NUMBER");
        		Score=Score-20;
        	}
        	else if(num>number)
        	{
        		System.out.println("YOUR NUMBER IS GREATER THAN ORIGINAL \nTRY ANOTHER NUMBER");
        		Score=Score-20;
        	}
        	else 
        	{
        		System.out.println("PLEASE ENTER THE NUMBER BETWEEN 1 TO 100 ONLY.");
        		Score=Score-20;
        	}
        	played++;
        	if(played==Chances)
        	{
        		System.out.println("\n\nSORRY YOUR CHANCES ARE OVER");
        		System.out.println("BETTER LUCK NEXT TIME");
        		System.out.println("YOUR SCORE IS "+Score);
        		System.out.println("THE ORIGINAL NUMBER IS "+number);
        		break;
        	}
        }
        sc.close();
     }
}