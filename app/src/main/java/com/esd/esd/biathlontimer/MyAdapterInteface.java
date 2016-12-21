package com.esd.esd.biathlontimer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 19.12.2016.
 */

public interface MyAdapterInteface
{

    void AddSportsman(Sportsman sportsman);
    void AddSportsmen(List<Sportsman> sportsmen);

    void RemoveSportsman(Sportsman sportsman);
    void RemoveSportsmen(List<Sportsman> sportsmen);

    void ChangeSportsman(Sportsman newSportsman, Sportsman oldSportsman);

    List<Sportsman> GetCheckedSportsmen();

    void ResetHaveMarkedFlag();

    void SortList(List<Sportsman> sortedList);
}
