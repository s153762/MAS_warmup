
import org.junit.Before;
import org.junit.Test;
import searchclient.Level;

import static org.junit.Assert.assertEquals;

public class TestLevel {
    private Level level;

    @Before
    public void before(){
        Level level = new Level(10);
        this.level = level;
        level.setWall(true,0,0);
        level.setGoal('X',0,1);
    }

    @Test
    public void testLevel(){
        assertEquals('X',level.getGoal(0,1));
        assertEquals(true,level.getWall(0,0));

        assertEquals(10,level.getMaxCol());
        assertEquals(50,level.getMaxRow());
    }
}
