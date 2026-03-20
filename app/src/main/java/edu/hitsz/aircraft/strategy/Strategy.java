package edu.hitsz.aircraft.strategy;

import edu.hitsz.bullet.BaseBullet;

import java.util.List;

public interface Strategy {
    List<BaseBullet> shoot(int LocationX, int LocationY, int SpeedY, int direction, int shootNum, int power);
}
