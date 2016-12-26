package com.esd.esd.biathlontimer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by Oleg on 25.12.2016.
 */

public class SportsmanButtonAdapter extends RecyclerView.Adapter<RecyclerViewLocalDatabaseAdapter.ViewHolder> implements IMyAdapter<MegaSportsman>
{
    private List<MegaSportsman> sportsmen;
    private Context _localContext;
    public SportsmanButtonAdapter(Context context, List<MegaSportsman> sportsmen)
    {
        _localContext = context;
        this.sportsmen = sportsmen;
    }
    @Override
    public void AddSportsmen(List<MegaSportsman> sportsmen) {

    }

    @Override
    public void RemoveSportsman(MegaSportsman sportsman) {

    }

    @Override
    public void RemoveSportsmen(List<MegaSportsman> sportsmen) {

    }

    @Override
    public void ChangeSportsman(MegaSportsman newSportsman, MegaSportsman oldSportsman) {

    }

    @Override
    public List<MegaSportsman> GetCheckedSportsmen() {
        return null;
    }

    @Override
    public void ResetHaveMarkedFlag() {

    }

    @Override
    public void SortList(List<MegaSportsman> sortedList) {

    }

    @Override
    public void AddSportsman(MegaSportsman sportsman)
    {
        sportsmen.add(sportsman);
    }


    @Override
    public RecyclerViewLocalDatabaseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerViewLocalDatabaseAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
