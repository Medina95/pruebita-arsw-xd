package edu.eci.arsw.blueprints.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.BluePrintsFiltred;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

public class Subsampling implements BluePrintsFiltred {


    @Override
    public Blueprint getFlat(Blueprint flat) {
        List<Point> pointList =new ArrayList<>(flat.getPoints());
        List<Point> pointListAns =new ArrayList<>();

        for(int i = 0;i<pointList.size();i++ ){
            if(i%2 == 0){
                pointListAns.add(pointList.get(i));
            }
        }

        Point[] pnts;
        pnts = pointListAns.toArray(new Point[0]);
        return new Blueprint(flat.getAuthor(),flat.getName(),pnts);
    }
}
