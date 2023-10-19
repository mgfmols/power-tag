package xeterios.powertag.players.comparers.comparators;


import xeterios.powertag.game.GamePlayer;
import xeterios.powertag.players.comparers.ComparatorChecker;
import xeterios.powertag.players.comparers.GameSortingType;

import java.util.ArrayList;
import java.util.Comparator;

public class GamePointComparator implements Comparator<GamePlayer>
{
    @Override
    public int compare(GamePlayer o1, GamePlayer o2)
    {
        if (o1 == null || o2 == null)
        {
            return 0;
        }
        ArrayList<GameSortingType> checkOrder = ComparatorChecker.getCompareOrder(GameSortingType.Points);
        return ComparatorChecker.getCompleteCompareValue(o1, o2, checkOrder);
    }
}


