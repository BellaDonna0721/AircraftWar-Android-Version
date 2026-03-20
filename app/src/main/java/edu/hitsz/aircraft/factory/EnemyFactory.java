package edu.hitsz.aircraft.factory;

import edu.hitsz.aircraft.AbstractEnemy;

public interface EnemyFactory {
    AbstractEnemy createEnemy(int SpeedY, int HP);
}
