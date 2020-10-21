package taxi.ratingen.retro.responsemodel;

import java.util.Comparator;

public class MyNameComp implements Comparator<Car> {
 
    @Override
    public int compare(Car e1, Car e2) {
        if(e1.id==e2.id){
            return 0;
        }
        return 1;

    }
}  