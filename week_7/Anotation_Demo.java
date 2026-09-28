package JAVA_practicals.week_7;
import java.lang.annotation.*;;

@interface notblank{

}
@interface MaxLength {
    int value();
}

class signupform{
    @notblank 
    @MaxLength(10)
    String name;

    @notblank 
    @MaxLength(50)
    String email;

    @notblank
    String Address;

    signupform(String name , String email , String Address){
        this.name = name;
        this.email = email;
        this.Address = Address;
        

    }

}



public class Anotation_Demo {
    
}
