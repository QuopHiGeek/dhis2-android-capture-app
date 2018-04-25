package com.dhis2.data.forms.dataentry.fields.spinner;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.dhis2.R;
import com.dhis2.data.forms.dataentry.fields.RowAction;
import com.dhis2.databinding.FormSpinnerBinding;

import org.hisp.dhis.android.core.option.OptionModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.BehaviorProcessor;
import io.reactivex.processors.FlowableProcessor;

/**
 * Created by ppajuelo on 07/11/2017.
 */

public class SpinnerHolder extends RecyclerView.ViewHolder implements AdapterView.OnItemSelectedListener {

    private final CompositeDisposable disposable;
    private final FlowableProcessor<RowAction> processor;
    private final AppCompatSpinner spinner;
    private final FormSpinnerBinding binding;
    private static String currentValue;

    @NonNull
    private BehaviorProcessor<SpinnerViewModel> model;

    SpinnerHolder(FormSpinnerBinding mBinding, FlowableProcessor<RowAction> processor, boolean isBackgroundTransparent) {
        super(mBinding.getRoot());
        this.binding = mBinding;
        this.spinner = mBinding.spinner;
        this.processor = processor;
        this.binding.setIsBgTransparent(isBackgroundTransparent);
        this.spinner.setOnItemSelectedListener(this);

        if (isBackgroundTransparent) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = spinner.getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, 0);
            a.recycle();
            binding.hintLabel.setTextColor(color);
            ViewCompat.setBackgroundTintList(spinner, ColorStateList.valueOf(color));
        } else {
            binding.hintLabel.setTextColor(ContextCompat.getColor(binding.hintLabel.getContext(), R.color.colorAccent));
            ViewCompat.setBackgroundTintList(spinner, ColorStateList.valueOf(
                    ContextCompat.getColor(spinner.getContext(), R.color.colorAccent)
            ));
        }


        this.disposable = new CompositeDisposable();

        model = BehaviorProcessor.create();
        disposable.add(model.subscribe(viewModel -> {
                    binding.setLabel(viewModel.label());
                    binding.setOptionSet(viewModel.optionSet());
                    binding.setInitialValue(viewModel.value());
                    currentValue = viewModel.value();
                }
                , t -> Log.d("DHIS_ERROR", t.getMessage())));

    }

    public void update(SpinnerViewModel viewModel) {
        model.onNext(viewModel);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        if (position > 0) {
            binding.hintLabel.setVisibility(View.VISIBLE);
            processor.onNext(
                    RowAction.create(model.getValue().uid(), ((OptionModel) adapterView.getItemAtPosition(position - 1)).displayName())
            );
        } else {
            binding.hintLabel.setVisibility(View.INVISIBLE);
            processor.onNext(
                    RowAction.create(model.getValue().uid(), null)
            );
        }
        if (view != null) {
            TypedValue typedValue = new TypedValue();
            TypedArray a = spinner.getContext().obtainStyledAttributes(typedValue.data, new int[]{R.attr.colorPrimary});
            int color = a.getColor(0, 0);
            a.recycle();
            ((TextView) view).setTextColor(binding.getIsBgTransparent() ? color : Color.WHITE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
