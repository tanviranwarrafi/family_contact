package com.rafi_studio.familycontact;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder> implements Filterable {

    private List<ContactModel> contactModelList;
    private Context context;

    private List<ContactModel> filtedModelList;

    public ContactAdapter(List<ContactModel> contactModelList, Context context) {
        this.contactModelList = contactModelList;
        this.context = context;
        this.filtedModelList = contactModelList;
    }

    @NonNull
    @Override
    public ContactAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.contact_list_item_layout, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ContactAdapter.ViewHolder viewHolder, int position) {
        int id = contactModelList.get(position).getId();
        String english_name = contactModelList.get(position).getEnglishName();
        String bangla_name = contactModelList.get(position).getBanglaName();
        String english_mobile_no = contactModelList.get(position).getEnglish_mobile_no();
        String bangla_mobile_no = contactModelList.get(position).getBangla_mobile_no();
        String designation = contactModelList.get(position).getDesignation();
        String email_address = contactModelList.get(position).getEmail();
        String imageURL = contactModelList.get(position).getImage();

        viewHolder.setData(imageURL, english_name, bangla_name, english_mobile_no, bangla_mobile_no, designation, email_address);
    }

    @Override
    public int getItemCount() {
        return contactModelList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView englishName;
        private TextView banglaName;
        private ImageView call;

        private String hello, emailSubject, emailDescription;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userImage = itemView.findViewById(R.id.userImage);
            banglaName = itemView.findViewById(R.id.user_banglaName);
            englishName = itemView.findViewById(R.id.user_englishName);
            call = itemView.findViewById(R.id.user_call);
        }

        public void setData(final String imageURL, final String english_name, final String bangla_name, final String english_mobile_no, final String bangla_mobile_no, final String designation, final String email_address) {

            banglaName.setText(english_name);
            englishName.setText(bangla_name);

            Glide.with(context.getApplicationContext())
                .load(imageURL)
                .override(150,150)
                .centerCrop()
                .placeholder(R.drawable.unknown_user_list_item)
                .into(userImage);

            userImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog userImageDialog = new Dialog(itemView.getContext());
                    userImageDialog.setContentView(R.layout.dialog_user_image);
                    userImageDialog.setCancelable(true);

                    userImageDialog.getWindow().getAttributes().windowAnimations = R.style.FadeInAndFadeOut;
                    userImageDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    userImageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    ImageView userFullImage = userImageDialog.findViewById(R.id.dialogUserImage_userImage);

                    Glide.with(context.getApplicationContext())
                            .load(imageURL)
                            .centerCrop()
                            .placeholder(R.drawable.unknown_user)
                            .into(userFullImage);

                    userImageDialog.show();
                }
            });

            call.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + english_mobile_no));
                    itemView.getContext().startActivity(callIntent);
                }
            });

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final Dialog userDetailsDialog = new Dialog(itemView.getContext());
                    userDetailsDialog.setContentView(R.layout.dialog_user_details);
                    userDetailsDialog.setCancelable(true);

                    userDetailsDialog.getWindow().getAttributes().windowAnimations = R.style.LeftToRight;

                    userDetailsDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                    TextView dialog_name = userDetailsDialog.findViewById(R.id.dialog_name);
                    TextView dialog_designation = userDetailsDialog.findViewById(R.id.dialog_designation);
                    TextView dialog_mobile_no = userDetailsDialog.findViewById(R.id.dialog_mobile_no);
                    TextView dialog_email_address = userDetailsDialog.findViewById(R.id.dialog_email_address);

                    String plusEightEight = itemView.getResources().getString(R.string.contactAdapter_dialogUserDetails_plusEightEight);

                    dialog_name.setText(bangla_name);
                    dialog_mobile_no.setText(plusEightEight + bangla_mobile_no);
                    if (designation.isEmpty()){
                        dialog_designation.setText(itemView.getContext().getString(R.string.contactAdapter_dialogUserDetails_noDesignationFound));
                    }else{
                        dialog_designation.setText(designation);
                    }

                    if (email_address.isEmpty()){
                        dialog_email_address.setText(itemView.getContext().getString(R.string.contactAdapter_dialogUserDetails_noEmailFound));
                    } else {
                        dialog_email_address.setText(email_address);
                    }

                    ImageView dialog_call = userDetailsDialog.findViewById(R.id.dialog_call);
                    ImageView dialog_sendMessage = userDetailsDialog.findViewById(R.id.dialog_message);
                    ImageView dialog_sendEmail = userDetailsDialog.findViewById(R.id.dialog_email);

                    dialog_call.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent callIntent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + english_mobile_no));
                            itemView.getContext().startActivity(callIntent);
                            userDetailsDialog.dismiss();

                        }
                    });

                    dialog_sendMessage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent sendSMS = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + english_mobile_no));
                            itemView.getContext().startActivity(sendSMS);
                            userDetailsDialog.dismiss();
                        }
                    });

                    hello = itemView.getResources().getString(R.string.contactAdapter_dialogUserDetails_helloEmail);
                    emailSubject = itemView.getResources().getString(R.string.contactAdapter_dialogUserDetails_yourEmailSubject);
                    emailDescription = itemView.getResources().getString(R.string.contactAdapter_dialogUserDetails_yourEmailDescription);

                    dialog_sendEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setData(Uri.parse("mailto:"));
                            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {email_address} );
                            intent.putExtra(Intent.EXTRA_SUBJECT,emailSubject);
                            intent.putExtra(Intent.EXTRA_TEXT,hello + " " + bangla_name + "!!!\n" + emailDescription);
                            intent.setType("message/rfc822");
                            Intent chooser = Intent.createChooser(intent,"Choose One...");
                            itemView.getContext().startActivity(chooser);
                        }
                    });

                    userDetailsDialog.show();
                }
            });
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                String charString = constraint.toString();

                if (charString.isEmpty()){
                    contactModelList = filtedModelList;
                }else{
                    List<ContactModel> filterList = new ArrayList<>();
                    for (ContactModel data : filtedModelList){
                        if ((data.getEnglishName().toUpperCase()).contains(charString.toUpperCase()) || (data.getBanglaName().toUpperCase()).contains(charString.toUpperCase())){
                            filterList.add(data);
                        }
                    }
                    contactModelList = filterList;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = contactModelList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                contactModelList = (List<ContactModel>) results.values;
                notifyDataSetChanged();
            }
        };

    }

}
