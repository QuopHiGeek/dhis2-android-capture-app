package org.dhis2.usescases.enrollment

import android.content.Context
import dagger.Module
import dagger.Provides
import org.dhis2.R
import org.dhis2.data.dagger.PerActivity
import org.dhis2.data.forms.RulesRepository
import org.dhis2.data.forms.dataentry.DataEntryRepository
import org.dhis2.data.forms.dataentry.DataEntryStore
import org.dhis2.data.forms.dataentry.EnrollmentRepository
import org.dhis2.data.forms.dataentry.ValueStore
import org.dhis2.data.forms.dataentry.ValueStoreImpl
import org.dhis2.data.forms.dataentry.fields.FieldViewModelFactoryImpl
import org.dhis2.data.schedulers.SchedulerProvider
import org.hisp.dhis.android.core.D2
import org.hisp.dhis.android.core.arch.repositories.`object`.ReadOnlyOneObjectRepositoryFinalImpl
import org.hisp.dhis.android.core.enrollment.EnrollmentObjectRepository
import org.hisp.dhis.android.core.program.Program
import org.hisp.dhis.android.core.trackedentity.TrackedEntityInstanceObjectRepository
import org.hisp.dhis.rules.RuleExpressionEvaluator

@Module
class EnrollmentModule(
    val enrollmentView: EnrollmentView,
    val enrollmentUid: String,
    val programUid: String
) {

    @Provides
    @PerActivity
    fun provideEnrollmentRepository(d2: D2): EnrollmentObjectRepository {
        return d2.enrollmentModule().enrollments().uid(enrollmentUid)
    }

    @Provides
    @PerActivity
    fun provideTeiRepository(
        d2: D2,
        enrollmentRepository: EnrollmentObjectRepository
    ): TrackedEntityInstanceObjectRepository {
        return d2.trackedEntityModule().trackedEntityInstances()
            .uid(enrollmentRepository.blockingGet().trackedEntityInstance())
    }

    @Provides
    @PerActivity
    fun provideProgramRepository(d2: D2): ReadOnlyOneObjectRepositoryFinalImpl<Program> {
        return d2.programModule().programs().uid(programUid)
    }

    @Provides
    @PerActivity
    fun provideDataEntrytRepository(context: Context, d2: D2): DataEntryRepository {
        val modelFactory = FieldViewModelFactoryImpl(
            context.getString(R.string.enter_text),
            context.getString(R.string.enter_long_text),
            context.getString(R.string.enter_number),
            context.getString(R.string.enter_integer),
            context.getString(R.string.enter_positive_integer),
            context.getString(R.string.enter_negative_integer),
            context.getString(R.string.enter_positive_integer_or_zero),
            context.getString(R.string.filter_options),
            context.getString(R.string.choose_date)
        )
        return EnrollmentRepository(context, modelFactory, enrollmentUid, d2)
    }

    @Provides
    @PerActivity
    fun providePresenter(
        d2: D2,
        enrollmentObjectRepository: EnrollmentObjectRepository,
        dataEntryRepository: DataEntryRepository,
        teiRepository: TrackedEntityInstanceObjectRepository,
        programRepository: ReadOnlyOneObjectRepositoryFinalImpl<Program>,
        schedulerProvider: SchedulerProvider,
        formRepository: EnrollmentFormRepository,
        valueStore: ValueStore
    ): EnrollmentPresenterImpl {
        return EnrollmentPresenterImpl(
            enrollmentView,
            d2,
            enrollmentObjectRepository,
            dataEntryRepository,
            teiRepository,
            programRepository,
            schedulerProvider,
            formRepository,
            valueStore
        )
    }

    @Provides
    @PerActivity
    fun valueStore(d2: D2, enrollmentRepository: EnrollmentObjectRepository): ValueStore {
        return ValueStoreImpl(
            d2,
            enrollmentRepository.blockingGet().trackedEntityInstance()!!,
            DataEntryStore.EntryMode.ATTR
        )
    }

    @Provides
    @PerActivity
    internal fun rulesRepository(d2: D2): RulesRepository {
        return RulesRepository(d2)
    }

    @Provides
    @PerActivity
    fun formRepository(
        d2: D2,
        rulesRepository: RulesRepository,
        evaluator: RuleExpressionEvaluator,
        enrollmentRepository: EnrollmentObjectRepository,
        programRepository: ReadOnlyOneObjectRepositoryFinalImpl<Program>
    ): EnrollmentFormRepository {
        return EnrollmentFormRepositoryImpl(
            d2,
            rulesRepository,
            evaluator,
            enrollmentRepository,
            programRepository
        )
    }
}
