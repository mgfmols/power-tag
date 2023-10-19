package xeterios.powertag.players.comparers.comparators;

import xeterios.powertag.players.PlayerData;
import xeterios.powertag.players.comparers.ComparatorChecker;
import xeterios.powertag.players.comparers.SortingType;

import java.util.ArrayList;
import java.util.Comparator;

public class UUIDComparator implements Comparator<PlayerData>
{

    @Override
    public int compare(PlayerData o1, PlayerData o2)
    {
        if (o1 == null || o2 == null)
        {
            return 0;
        }
        ArrayList<SortingType> checkOrder = ComparatorChecker.getCompareOrder(SortingType.UUID);
        return ComparatorChecker.getCompleteCompareValue(o1, o2, checkOrder);
    }
}
