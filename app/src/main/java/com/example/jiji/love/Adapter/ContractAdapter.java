package com.example.jiji.love.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jiji.love.JavaBean.Contract;
import com.example.jiji.love.R;
import com.example.jiji.love.UI.ChatRoomActivity;

import java.util.List;

/**
 * Created by jiji on 2017/4/16.
 * 会话列表适配器
 */

public class ContractAdapter extends RecyclerView.Adapter<ContractAdapter.ViewHolder> {
    private List<Contract> mcontractList;

    static class ViewHolder extends RecyclerView.ViewHolder {
        View contractView;
        ImageView contractHead;
        TextView contractName;
        TextView topMessage;

        public ViewHolder(View view) {
            super(view);
            contractView = view;
            contractHead = (ImageView) view.findViewById(R.id.contract_head);
            contractName = (TextView) view.findViewById(R.id.contract_name);
            topMessage = (TextView) view.findViewById(R.id.contrect_topmessage);
        }


    }

    public ContractAdapter(List<Contract> contractList) {
        mcontractList = contractList;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_contract, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.contractView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Contract contract = mcontractList.get(position);
                Intent intent = new Intent(parent.getContext(), ChatRoomActivity.class);
                String name = contract.getName();
                intent.putExtra("name", name);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                Context context = parent.getContext();
                context.startActivity(intent);
//                Toast.makeText(v.getContext(), "test---name:"
//                       + contract.getName() + "---topMessage:" + contract.getTopMessage(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Contract contract = mcontractList.get(position);
        holder.contractName.setText(contract.getName());
        holder.topMessage.setText(contract.getTopMessage());
    }

    @Override
    public int getItemCount() {
        return mcontractList.size();
    }
}
