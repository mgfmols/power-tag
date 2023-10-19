package xeterios.powertag.players.comparers;

import xeterios.powertag.game.GamePlayer;
import xeterios.powertag.players.PlayerData;

import java.util.ArrayList;

public class ComparatorChecker
{

    public static ArrayList<SortingType> getCompareOrder(SortingType input)
    {
        ArrayList<SortingType> order = new ArrayList<>();
        order.add(input);
        for (SortingType type : SortingType.values())
        {
            if (!type.equals(input))
            {
                order.add(type);
            }
        }
        return order;
    }

    public static int getCompareValue(SortingType type, PlayerData o1, PlayerData o2)
    {
        return switch (type)
                {
                    case UUID -> o1.getUuid().compareTo(o2.getUuid());
                    case Points -> Integer.compare(o2.getTotalPoints(), o1.getTotalPoints());
                    case Wins -> Integer.compare(o2.getTotalWins(), o1.getTotalWins());
                    case Winstreak -> Integer.compare(o2.getWinStreak(), o1.getWinStreak());
                };
    }

    public static int getCompleteCompareValue(PlayerData o1, PlayerData o2, ArrayList<SortingType> checkOrder)
    {
        int value = 0;
        boolean continueCheck = true;
        for (int i = 0; i < checkOrder.size() && continueCheck; i++)
        {
            int checkValue = ComparatorChecker.getCompareValue(checkOrder.get(i), o1, o2);
            if (checkValue < 0)
            {
                checkValue = -1;
            }
            else if (checkValue > 0)
            {
                checkValue = 1;
            }
            value = checkValue;
            continueCheck = checkValue == 0;
        }
        return value;
    }

    public static ArrayList<GameSortingType> getCompareOrder(GameSortingType input)
    {
        ArrayList<GameSortingType> order = new ArrayList<>();
        order.add(input);
        for (GameSortingType type : GameSortingType.values())
        {
            if (!type.equals(input))
            {
                order.add(type);
            }
        }
        return order;
    }

    public static int getCompareValue(GameSortingType type, GamePlayer o1, GamePlayer o2)
    {
        return switch (type)
                {
                    case Points -> Integer.compare(o2.getPoints(), o1.getPoints());
                    case Bonuspoints -> Integer.compare(o2.getBonusPoints(), o1.getBonusPoints());
                };
    }

    public static int getCompleteCompareValue(GamePlayer o1, GamePlayer o2, ArrayList<GameSortingType> checkOrder)
    {
        int value = 0;
        boolean continueCheck = true;
        for (int i = 0; i < checkOrder.size() && continueCheck; i++)
        {
            int checkValue = ComparatorChecker.getCompareValue(checkOrder.get(i), o1, o2);
            if (checkValue < 0)
            {
                checkValue = -1;
            }
            else if (checkValue > 0)
            {
                checkValue = 1;
            }
            value = checkValue;
            continueCheck = checkValue == 0;
        }
        return value;
    }
}
