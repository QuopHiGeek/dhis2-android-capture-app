package org.dhis2.data.forms.dataentry.fields.orgUnit;

import android.graphics.Color;

import androidx.core.content.ContextCompat;

import org.dhis2.R;
import org.dhis2.data.forms.dataentry.fields.FormViewHolder;
import org.dhis2.data.forms.dataentry.fields.RowAction;
import org.dhis2.databinding.FormOrgUnitBinding;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.processors.FlowableProcessor;

import static android.text.TextUtils.isEmpty;

/**
 * QUADRAM. Created by ppajuelo on 19/03/2018.
 */

public class OrgUnitHolder extends FormViewHolder {
    private final FormOrgUnitBinding binding;
    private CompositeDisposable compositeDisposable;
    private OrgUnitViewModel model;

    OrgUnitHolder(FormOrgUnitBinding binding, FlowableProcessor<RowAction> processor, boolean isSearchMode) {
        super(binding);
        this.binding = binding;
        compositeDisposable = new CompositeDisposable();

        binding.orgUnitView.setListener(orgUnitUid -> {
            processor.onNext(RowAction.create(model.uid(), orgUnitUid, getAdapterPosition()));
            if (!isSearchMode)
                itemView.setBackgroundColor(Color.WHITE);
        });
    }

    @Override
    public void dispose() {
        compositeDisposable.clear();
    }

    @Override
    public void performAction() {
        itemView.setBackground(ContextCompat.getDrawable(itemView.getContext(), R.drawable.item_selected_bg));
        binding.orgUnitView.performOnFocusAction();
    }

    public void update(OrgUnitViewModel viewModel) {
        this.model = viewModel;
        String uid_value_name = viewModel.value();
        String ouUid = null;
        String ouName = null;
        if (!isEmpty(uid_value_name)) {
            ouUid = uid_value_name.split("_ou_")[0];
            ouName = uid_value_name.split("_ou_")[1];
        }

        binding.orgUnitView.setObjectStyle(viewModel.objectStyle());
        binding.orgUnitView.setLabel(viewModel.label(), viewModel.mandatory());
        descriptionText = viewModel.description();
        binding.orgUnitView.setDescription(descriptionText);
        binding.orgUnitView.setWarning(viewModel.warning(), viewModel.error());
        binding.orgUnitView.setValue(ouUid, ouName);
        binding.orgUnitView.getEditText().setText(ouName);
        binding.orgUnitView.updateEditable(viewModel.editable());
        label = new StringBuilder().append(viewModel.label());

    }
}
