package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BluePrintsFiltred;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class Redundance implements BluePrintsFiltred {


    @Override
    public Blueprint getFlat(Blueprint flat) {
        List<Point> listFiltr =new ArrayList<>(flat.getPoints());
        int count = 0;
        while(count < listFiltr.size() -1){
            int index = count +1;
            if(listFiltr.get(count).getX() == listFiltr.get(index).getX() && listFiltr.get(count).getY() == listFiltr.get(index).getY()){
                listFiltr.remove(count);
            }else{
                count++;
            }
        }
        Point[] pnts;
        pnts = listFiltr.toArray(new Point[0]);
        return new Blueprint(flat.getAuthor(),flat.getName(),pnts);
    }
}
