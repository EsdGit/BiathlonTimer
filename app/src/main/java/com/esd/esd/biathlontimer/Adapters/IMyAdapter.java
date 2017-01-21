package com.esd.esd.biathlontimer.Adapters;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Oleg on 19.12.2016.
 */

public interface IMyAdapter<T>
{

    void AddSportsman(T sportsman);
    void AddSportsmen(List<T> sportsmen);

    void RemoveSportsman(T sportsman);
    void RemoveSportsmen(List<T> sportsmen);

    boolean ChangeSportsman(T newSportsman, T oldSportsman);

    List<T> GetCheckedSportsmen();

    void ResetHaveMarkedFlag();

    void SortList(List<T> sortedList);
}
