/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.eci.arsw.blueprints.test.persistence.impl;

import edu.eci.arsw.blueprints.model.Blueprint;
import edu.eci.arsw.blueprints.model.Point;
import edu.eci.arsw.blueprints.persistence.impl.Redundance;
import edu.eci.arsw.blueprints.persistence.impl.Subsampling;
import edu.eci.arsw.blueprints.persistence.BlueprintNotFoundException;
import edu.eci.arsw.blueprints.persistence.BlueprintPersistenceException;
import edu.eci.arsw.blueprints.persistence.BlueprintsPersistence;
import edu.eci.arsw.blueprints.persistence.impl.InMemoryBlueprintPersistence;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import static org.junit.Assert.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 *
 * @author hcadavid
 */
public class InMemoryPersistenceTest {

    private BlueprintsPersistence persistence;

    @Before
    public void setUp(){
        ApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
        persistence = ac.getBean(BlueprintsPersistence.class);
    }
    
    @Test
    public void saveNewAndLoadTest() throws BlueprintPersistenceException, BlueprintNotFoundException{
        InMemoryBlueprintPersistence ibpp=new InMemoryBlueprintPersistence();

        Point[] pts0=new Point[]{new Point(40, 40),new Point(15, 15)};
        Blueprint bp0=new Blueprint("mack", "mypaint",pts0);
        
        ibpp.saveBlueprint(bp0);
        
        Point[] pts=new Point[]{new Point(0, 0),new Point(10, 10)};
        Blueprint bp=new Blueprint("john", "thepaint",pts);
        
        ibpp.saveBlueprint(bp);
        
        assertNotNull("Loading a previously stored blueprint returned null.",ibpp.getBlueprint(bp.getAuthor(), bp.getName()));
        
        assertEquals("Loading a previously stored blueprint returned a different blueprint.",ibpp.getBlueprint(bp.getAuthor(), bp.getName()), bp);
        
    }


    @Test
    public void saveExistingBpTest() {
        InMemoryBlueprintPersistence ibpp=new InMemoryBlueprintPersistence();
        
        Point[] pts=new Point[]{new Point(0, 0),new Point(10, 10)};
        Blueprint bp=new Blueprint("john", "thepaint",pts);
        
        try {
            ibpp.saveBlueprint(bp);
        } catch (BlueprintPersistenceException ex) {
            fail("Blueprint persistence failed inserting the first blueprint.");
        }
        
        Point[] pts2=new Point[]{new Point(10, 10),new Point(20, 20)};
        Blueprint bp2=new Blueprint("john", "thepaint",pts2);

        try{
            ibpp.saveBlueprint(bp2);
            fail("An exception was expected after saving a second blueprint with the same name and autor");
        }
        catch (BlueprintPersistenceException ex){
            
        }
                
        
    }

    @Test
    public void getBlueprintShouldReturnExistingBlueprint() throws BlueprintNotFoundException, BlueprintPersistenceException {
        // Arrange
        Blueprint expectedBlueprint = new Blueprint("Christian", "arsw");
        persistence.saveBlueprint(expectedBlueprint);

        // Act
        Blueprint actualBlueprint = persistence.getBlueprint("Christian", "arsw");

        // Assert
        assertEquals(expectedBlueprint, actualBlueprint);
    }

    @Test
    public void getBlueprintShouldThrowNotFoundExceptionForNonExistingBlueprint() {
        // Assert
        assertThrows(BlueprintNotFoundException.class, () -> {
            // Act
            persistence.getBlueprint("nonexistingauthor", "nonexistingblueprint");
        });
    }

    @Test
    public void getBlueprintsByAuthorShouldReturnBlueprintsForExistingAuthor() throws BlueprintNotFoundException, BlueprintPersistenceException {
        Blueprint expectedBlueprint1 = new Blueprint("author1", "blueprint1");
        Blueprint expectedBlueprint2 = new Blueprint("author1", "blueprint2");
        persistence.saveBlueprint(expectedBlueprint1);
        persistence.saveBlueprint(expectedBlueprint2);

        Set<Blueprint> blueprints = persistence.getBlueprintsByAuthor("author1");

        assertTrue(blueprints.contains(expectedBlueprint1));
        assertTrue(blueprints.contains(expectedBlueprint2));
    }

    @Test
    public void getBlueprintsByAuthorShouldReturnNotSetForNonExistingAuthor() {
        // Act
        Set<Blueprint> blueprints = null;
        try {
            blueprints = persistence.getBlueprintsByAuthor("nonexistingauthor");
        } catch (BlueprintNotFoundException ignored) {

        }

        assertNull(blueprints);
    }

    @Test
    public void filtredAbpBySubsampling(){
        Subsampling filtre = new Subsampling();

        Point[] points = new Point[]{new Point(3, 2),new Point(3,2),new Point(10,3)};

        Point[] points1 = new Point[]{new Point(3, 2), new Point(5, 5), new Point(10, 3), new Point(8, 9)};

        Point[] points2 = new Point[]{new Point(3, 2), new Point(3, 2), new Point(5, 5), new Point(10, 3), new Point(7, 8)};

        Point[] points3 = new Point[]{new Point(3, 2), new Point(3, 2), new Point(5, 5), new Point(5, 5), new Point(5, 5), new Point(10, 3), new Point(7, 7)};

        Point[] points4 = new Point[]{new Point(3, 2), new Point(3, 2), new Point(3, 2),  new Point(5, 5), new Point(5, 5), new Point(5, 5), new Point(10, 3), new Point(10, 3), new Point(10, 3), new Point(4, 6), new Point(4, 6)};

        Blueprint blueprintTest = filtre.getFlat(new Blueprint("","",points));
        Blueprint blueprintTest1 = filtre.getFlat(new Blueprint("","",points1));
        Blueprint blueprintTest2 = filtre.getFlat(new Blueprint("","",points2));
        Blueprint blueprintTest3 = filtre.getFlat(new Blueprint("","",points3));
        Blueprint blueprintTest4 = filtre.getFlat(new Blueprint("","",points4));

        assertEquals("[ (3, 2) (10, 3) ]",blueprintTest.pointsToString());
        assertEquals("[ (3, 2) (10, 3) ]",blueprintTest1.pointsToString());
        assertEquals("[ (3, 2) (5, 5) (7, 8) ]",blueprintTest2.pointsToString());
        assertEquals("[ (3, 2) (5, 5) (5, 5) (7, 7) ]",blueprintTest3.pointsToString());
        assertEquals("[ (3, 2) (3, 2) (5, 5) (10, 3) (10, 3) (4, 6) ]",blueprintTest4.pointsToString());
    }


    @Test
    public void filtredBpByRedundance(){
        Redundance filtre = new Redundance();

        Point[] points = new Point[]{new Point(3, 2),new Point(3,2),new Point(10,3)};

        Point[] points1 = new Point[]{new Point(3, 2), new Point(5, 5), new Point(10, 3), new Point(8, 9)};

        Point[] points2 = new Point[]{new Point(3, 2), new Point(3, 2), new Point(5, 5), new Point(10, 3), new Point(7, 8)};

        Point[] points3 = new Point[]{new Point(3, 2), new Point(3, 2), new Point(5, 5), new Point(5, 5), new Point(5, 5), new Point(10, 3), new Point(7, 7)};

        Point[] points4 = new Point[]{new Point(3, 2), new Point(3, 2), new Point(3, 2), new Point(1, 5), new Point(5, 5), new Point(5, 5), new Point(5, 5),new Point(1, 5), new Point(10, 3), new Point(10, 3), new Point(10, 3), new Point(1, 5),  new Point(4, 6), new Point(4, 6)};

        Blueprint blueprintTest = filtre.getFlat(new Blueprint("","",points));
        Blueprint blueprintTest1 = filtre.getFlat(new Blueprint("","",points1));
        Blueprint blueprintTest2 = filtre.getFlat(new Blueprint("","",points2));
        Blueprint blueprintTest3 = filtre.getFlat(new Blueprint("","",points3));
        Blueprint blueprintTest4 = filtre.getFlat(new Blueprint("","",points4));

        assertEquals("[ (3, 2) (10, 3) ]",blueprintTest.pointsToString());
        assertEquals("[ (3, 2) (5, 5) (10, 3) (8, 9) ]",blueprintTest1.pointsToString());
        assertEquals("[ (3, 2) (5, 5) (10, 3) (7, 8) ]",blueprintTest2.pointsToString());
        assertEquals("[ (3, 2) (5, 5) (10, 3) (7, 7) ]",blueprintTest3.pointsToString());
        assertEquals("[ (3, 2) (1, 5) (5, 5) (1, 5) (10, 3) (1, 5) (4, 6) ]",blueprintTest4.pointsToString());
    }
}
