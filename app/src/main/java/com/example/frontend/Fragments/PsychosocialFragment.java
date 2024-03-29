package com.example.frontend.Fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.frontend.Fragments.Dialogs.ReasonDialog;
import com.example.frontend.Models.ImprovementReason;
import com.example.frontend.Models.PsychoSocialAfter;
import com.example.frontend.Models.PsychoSocialBefore;
import com.example.frontend.R;
import com.example.frontend.Service.JsonPlaceHolderApi;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PsychosocialFragment extends Fragment implements ReasonDialog.ReasonDialogListener {

    private int patientId;

    private Button btnPainBefore;
    private Button btnFamilyBefore;
    private Button btnWorkBefore;
    private Button btnFinancialBefore;
    private Button btnEventBefore;

    private Button btnPainAfter;
    private Button btnFamilyAfter;
    private Button btnWorkAfter;
    private Button btnFinancialAfter;
    private Button btnEventAfter;

    private ImageView btnReason;

    private int xDelta;
    private int yDelta;
    private RelativeLayout rlActual;

    private ImprovementReason improvementReasonOfPatient = new ImprovementReason();
    private PsychoSocialBefore psychoSocialBeforeOfPatient = new PsychoSocialBefore();
    private PsychoSocialAfter psychoSocialAfterOfPatient = new PsychoSocialAfter();

    private RelativeLayout.LayoutParams lpPainBefore;
    private RelativeLayout.LayoutParams lpPainAfter;

    private RelativeLayout.LayoutParams lpFamilyBefore;
    private RelativeLayout.LayoutParams lpFamilyAfter;

    private RelativeLayout.LayoutParams lpWorkBefore;
    private RelativeLayout.LayoutParams lpWorkAfter;

    private RelativeLayout.LayoutParams lpFinancialBefore;
    private RelativeLayout.LayoutParams lpFinancialAfter;

    private RelativeLayout.LayoutParams lpEventBefore;
    private RelativeLayout.LayoutParams lpEventAfter;

    private boolean initialSetUpBeforeDone = false;
    private boolean initialSetUpAfterDone = false;

    Retrofit retrofit = new Retrofit.Builder().baseUrl("https://consapp.herokuapp.com/api/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    JsonPlaceHolderApi jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        patientId = getArguments().getInt("patientId");

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_psychosocial, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        rlActual = view.findViewById(R.id.rlBefore);

        btnPainBefore = view.findViewById(R.id.btnPainBefore);
        btnPainBefore.setOnTouchListener(new ChoiceTouchListener());
        btnFamilyBefore = view.findViewById(R.id.btnFamilyBefore);
        btnFamilyBefore.setOnTouchListener(new ChoiceTouchListener());
        btnWorkBefore = view.findViewById(R.id.btnWorkBefore);
        btnWorkBefore.setOnTouchListener(new ChoiceTouchListener());
        btnFinancialBefore = view.findViewById(R.id.btnFinancialBefore);
        btnFinancialBefore.setOnTouchListener(new ChoiceTouchListener());
        btnEventBefore = view.findViewById(R.id.btnEventBefore);
        btnEventBefore.setOnTouchListener(new ChoiceTouchListener());

        btnPainAfter = view.findViewById(R.id.btnPainAfter);
        btnPainAfter.setOnTouchListener(new ChoiceTouchListener());
        btnFamilyAfter = view.findViewById(R.id.btnFamilyAfter);
        btnFamilyAfter.setOnTouchListener(new ChoiceTouchListener());
        btnWorkAfter = view.findViewById(R.id.btnWorkAfter);
        btnWorkAfter.setOnTouchListener(new ChoiceTouchListener());
        btnFinancialAfter = view.findViewById(R.id.btnFinancialAfter);
        btnFinancialAfter.setOnTouchListener(new ChoiceTouchListener());
        btnEventAfter = view.findViewById(R.id.btnEventAfter);
        btnEventAfter.setOnTouchListener(new ChoiceTouchListener());

        btnReason = view.findViewById(R.id.btnReason);
        btnReason.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReasonDialog();
            }
        });

        setPsychoSocialBefore();
        setPsychoSocialAfter();

    }

    private final class ChoiceTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            int X = (int) motionEvent.getRawX();
            int Y = (int) motionEvent.getRawY();
            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    xDelta = X - lParams.leftMargin;
                    yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    savePositions();
                    setPsychoSocialBefore();
                    setPsychoSocialAfter();
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    break;
                case MotionEvent.ACTION_MOVE:
                    int rlWidth = rlActual.getWidth() - 70;
                    int rlHeight = rlActual.getHeight() - 70;

                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view.getLayoutParams();
                    int newX = X - xDelta;
                    int newY = Y - yDelta;
                    if ((newX >= 0 && newX <= rlWidth && newY >= 0 && newY <= rlHeight)) {
                        layoutParams.leftMargin = newX;
                        layoutParams.topMargin = newY;
                        layoutParams.rightMargin = -250;
                        layoutParams.bottomMargin = -250;
                        view.setLayoutParams(layoutParams);
                    }
                    break;
            }
            rlActual.invalidate();
            return true;
        }
    }

    public void openReasonDialog() {
        ReasonDialog reasonDialog = new ReasonDialog();
        Bundle args = new Bundle();
        args.putInt("patient_id", patientId);
        reasonDialog.setArguments(args);
        reasonDialog.setTargetFragment(PsychosocialFragment.this, 1);
        reasonDialog.show(getActivity().getSupportFragmentManager(), "Reason Dialog");
    }

    @Override
    public void applyTexts(boolean drugsReason, boolean exercisesReason, boolean awarenessReason, boolean otherReasons, String otherReasonsText) {
        improvementReasonOfPatient.setPatient_id(patientId);
        improvementReasonOfPatient.setDrugs(drugsReason);
        improvementReasonOfPatient.setExercises(exercisesReason);
        improvementReasonOfPatient.setAwareness(awarenessReason);
        improvementReasonOfPatient.setOther_reason(otherReasons);
        improvementReasonOfPatient.setOther_reason_text(otherReasonsText);

        setImprovementReason();
    }

    public void addNewImprovementReason(ImprovementReason improvementReason) {
        Call<ResponseBody> call = jsonPlaceHolderApi.createImprovementReason(improvementReason);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "createNote NOT successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updateImprovementReason(final ImprovementReason updatedImprovementReason) {
        Call<ResponseBody> call = jsonPlaceHolderApi.updateImprovementReason(patientId, updatedImprovementReason);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "createImprovementReason NOT successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setImprovementReason() {
        Call<Boolean> call = jsonPlaceHolderApi.existsImprovementReason(patientId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(getActivity(), "Get improvement reason not successful", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    boolean improvementReasonExists = response.body();
                    if (improvementReasonExists) {
                        updateImprovementReason(improvementReasonOfPatient);
                    } else {
                        addNewImprovementReason(improvementReasonOfPatient);
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    public void addNewPsychoSocialBefore(PsychoSocialBefore psychoSocialBefore) {
        Call<ResponseBody> call = jsonPlaceHolderApi.createPsychoSocialBefore(psychoSocialBefore);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "createNote NOT successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePsychoSocialBefore(final PsychoSocialBefore updatedPsychoSocialBefore) {
        Call<ResponseBody> call = jsonPlaceHolderApi.updatePsychoSocialBefore(patientId, updatedPsychoSocialBefore);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "createPsychoSocialBefore NOT successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setPsychoSocialBefore() {
        Call<Boolean> call = jsonPlaceHolderApi.existsPsychoSocialBefore(patientId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    return;
                } else {
                    boolean PsychoSocialBeforeExists = response.body();

                    if (PsychoSocialBeforeExists) {
                        if(!initialSetUpBeforeDone){
                            setUpPositionsBefore();
                            initialSetUpBeforeDone = true;
                        }else{
                            updatePsychoSocialBefore(psychoSocialBeforeOfPatient);
                        }
                    } else {
                        if(initialSetUpBeforeDone){
                            addNewPsychoSocialBefore(psychoSocialBeforeOfPatient);
                        }else{
                            initialSetUpBeforeDone = true;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addNewPsychoSocialAfter(PsychoSocialAfter psychoSocialAfter) {
        Call<ResponseBody> call = jsonPlaceHolderApi.createPsychoSocialAfter(psychoSocialAfter);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "createNote NOT successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void updatePsychoSocialAfter(final PsychoSocialAfter updatedPsychoSocialAfter) {
        Call<ResponseBody> call = jsonPlaceHolderApi.updatePsychoSocialAfter(patientId, updatedPsychoSocialAfter);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getActivity(), "createPsychoSocialAfter NOT successful", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setPsychoSocialAfter() {
        Call<Boolean> call = jsonPlaceHolderApi.existsPsychoSocialAfter(patientId);
        call.enqueue(new Callback<Boolean>() {
            @Override
            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                if (!response.isSuccessful()) {
                    return;
                } else {
                    boolean PsychoSocialAfterExists = response.body();

                    if (PsychoSocialAfterExists) {
                        if(!initialSetUpAfterDone){
                            setUpPositionsAfter();
                            initialSetUpAfterDone = true;
                        }else{
                            updatePsychoSocialAfter(psychoSocialAfterOfPatient);
                        }
                    } else {
                        if(initialSetUpAfterDone) {
                            addNewPsychoSocialAfter(psychoSocialAfterOfPatient);
                        }else{
                            initialSetUpAfterDone = true;
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<Boolean> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void savePositions(){
        psychoSocialBeforeOfPatient.setPatient_id(patientId);
        psychoSocialAfterOfPatient.setPatient_id(patientId);

        lpPainBefore = (RelativeLayout.LayoutParams) btnPainBefore.getLayoutParams();
        psychoSocialBeforeOfPatient.setPain_xpos(lpPainBefore.leftMargin);
        psychoSocialBeforeOfPatient.setPain_ypos(lpPainBefore.topMargin);

        lpFamilyBefore = (RelativeLayout.LayoutParams) btnFamilyBefore.getLayoutParams();
        psychoSocialBeforeOfPatient.setFamily_xpos(lpFamilyBefore.leftMargin);
        psychoSocialBeforeOfPatient.setFamily_ypos(lpFamilyBefore.topMargin);

        lpWorkBefore = (RelativeLayout.LayoutParams) btnWorkBefore.getLayoutParams();
        psychoSocialBeforeOfPatient.setWork_xpos(lpWorkBefore.leftMargin);
        psychoSocialBeforeOfPatient.setWork_ypos(lpWorkBefore.topMargin);

        lpFinancialBefore = (RelativeLayout.LayoutParams) btnFinancialBefore.getLayoutParams();
        psychoSocialBeforeOfPatient.setFinance_xpos(lpFinancialBefore.leftMargin);
        psychoSocialBeforeOfPatient.setFinance_ypos(lpFinancialBefore.topMargin);

        lpEventBefore = (RelativeLayout.LayoutParams) btnEventBefore.getLayoutParams();
        psychoSocialBeforeOfPatient.setEvent_xpos(lpEventBefore.leftMargin);
        psychoSocialBeforeOfPatient.setEvent_ypos(lpEventBefore.topMargin);



        lpPainAfter = (RelativeLayout.LayoutParams) btnPainAfter.getLayoutParams();
        psychoSocialAfterOfPatient.setPain_xpos(lpPainAfter.leftMargin);
        psychoSocialAfterOfPatient.setPain_ypos(lpPainAfter.topMargin);

        lpFamilyAfter = (RelativeLayout.LayoutParams) btnFamilyAfter.getLayoutParams();
        psychoSocialAfterOfPatient.setFamily_xpos(lpFamilyAfter.leftMargin);
        psychoSocialAfterOfPatient.setFamily_ypos(lpFamilyAfter.topMargin);

        lpWorkAfter = (RelativeLayout.LayoutParams) btnWorkAfter.getLayoutParams();
        psychoSocialAfterOfPatient.setWork_xpos(lpWorkAfter.leftMargin);
        psychoSocialAfterOfPatient.setWork_ypos(lpWorkAfter.topMargin);

        lpFinancialAfter = (RelativeLayout.LayoutParams) btnFinancialAfter.getLayoutParams();
        psychoSocialAfterOfPatient.setFinance_xpos(lpFinancialAfter.leftMargin);
        psychoSocialAfterOfPatient.setFinance_ypos(lpFinancialAfter.topMargin);

        lpEventAfter = (RelativeLayout.LayoutParams) btnEventAfter.getLayoutParams();
        psychoSocialAfterOfPatient.setEvent_xpos(lpEventAfter.leftMargin);
        psychoSocialAfterOfPatient.setEvent_ypos(lpEventAfter.topMargin);
    }

    private void setUpPositionsBefore(){
        Call<PsychoSocialBefore> call = jsonPlaceHolderApi.getPsychoSocialBefore(patientId);
        call.enqueue(new Callback<PsychoSocialBefore>() {
            @Override
            public void onResponse(Call<PsychoSocialBefore> call, Response<PsychoSocialBefore> response) {
                if (!response.isSuccessful()) {
                    return;
                } else {
                    PsychoSocialBefore psychoSocialBefore = response.body();

                    lpPainBefore = (RelativeLayout.LayoutParams) btnPainBefore.getLayoutParams();
                    lpPainBefore.leftMargin = psychoSocialBefore.getPain_xpos();
                    lpPainBefore.topMargin = psychoSocialBefore.getPain_ypos();
                    btnPainBefore.setLayoutParams(lpPainBefore);

                    lpFamilyBefore = (RelativeLayout.LayoutParams) btnFamilyBefore.getLayoutParams();
                    lpFamilyBefore.leftMargin = psychoSocialBefore.getFamily_xpos();
                    lpFamilyBefore.topMargin = psychoSocialBefore.getFamily_ypos();
                    btnFamilyBefore.setLayoutParams(lpFamilyBefore);

                    lpWorkBefore = (RelativeLayout.LayoutParams) btnWorkBefore.getLayoutParams();
                    lpWorkBefore.leftMargin = psychoSocialBefore.getWork_xpos();
                    lpWorkBefore.topMargin = psychoSocialBefore.getWork_ypos();
                    btnWorkBefore.setLayoutParams(lpWorkBefore);

                    lpFinancialBefore = (RelativeLayout.LayoutParams) btnFinancialBefore.getLayoutParams();
                    lpFinancialBefore.leftMargin = psychoSocialBefore.getFinance_xpos();
                    lpFinancialBefore.topMargin = psychoSocialBefore.getFinance_ypos();
                    btnFinancialBefore.setLayoutParams(lpFinancialBefore);

                    lpEventBefore = (RelativeLayout.LayoutParams) btnEventBefore.getLayoutParams();
                    lpEventBefore.leftMargin = psychoSocialBefore.getEvent_xpos();
                    lpEventBefore.topMargin = psychoSocialBefore.getEvent_ypos();
                    btnEventBefore.setLayoutParams(lpEventBefore);
                }
            }

            @Override
            public void onFailure(Call<PsychoSocialBefore> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpPositionsAfter(){
        Call<PsychoSocialAfter> call = jsonPlaceHolderApi.getPsychoSocialAfter(patientId);
        call.enqueue(new Callback<PsychoSocialAfter>() {
            @Override
            public void onResponse(Call<PsychoSocialAfter> call, Response<PsychoSocialAfter> response) {
                if (!response.isSuccessful()) {
                    return;
                } else {
                    PsychoSocialAfter psychoSocialAfter = response.body();

                    lpPainAfter = (RelativeLayout.LayoutParams) btnPainAfter.getLayoutParams();
                    lpPainAfter.leftMargin = psychoSocialAfter.getPain_xpos();
                    lpPainAfter.topMargin = psychoSocialAfter.getPain_ypos();
                    btnPainAfter.setLayoutParams(lpPainAfter);

                    lpFamilyAfter = (RelativeLayout.LayoutParams) btnFamilyAfter.getLayoutParams();
                    lpFamilyAfter.leftMargin = psychoSocialAfter.getFamily_xpos();
                    lpFamilyAfter.topMargin = psychoSocialAfter.getFamily_ypos();
                    btnFamilyAfter.setLayoutParams(lpFamilyAfter);

                    lpWorkAfter = (RelativeLayout.LayoutParams) btnWorkAfter.getLayoutParams();
                    lpWorkAfter.leftMargin = psychoSocialAfter.getWork_xpos();
                    lpWorkAfter.topMargin = psychoSocialAfter.getWork_ypos();
                    btnWorkAfter.setLayoutParams(lpWorkAfter);

                    lpFinancialAfter = (RelativeLayout.LayoutParams) btnFinancialAfter.getLayoutParams();
                    lpFinancialAfter.leftMargin = psychoSocialAfter.getFinance_xpos();
                    lpFinancialAfter.topMargin = psychoSocialAfter.getFinance_ypos();
                    btnFinancialAfter.setLayoutParams(lpFinancialAfter);

                    lpEventAfter = (RelativeLayout.LayoutParams) btnEventAfter.getLayoutParams();
                    lpEventAfter.leftMargin = psychoSocialAfter.getEvent_xpos();
                    lpEventAfter.topMargin = psychoSocialAfter.getEvent_ypos();
                    btnEventAfter.setLayoutParams(lpEventAfter);
                }
            }

            @Override
            public void onFailure(Call<PsychoSocialAfter> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
