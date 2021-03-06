package org.jobrunr.scheduling;

import org.jobrunr.jobs.lambdas.IocJobLambda;
import org.jobrunr.jobs.lambdas.IocJobLambdaFromStream;
import org.jobrunr.jobs.lambdas.JobLambda;
import org.jobrunr.jobs.lambdas.JobLambdaFromStream;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Provides static methods for creating fire-and-forget, delayed and recurring jobs as well as to delete existing background jobs.
 *
 * @author Ronald Dehuysser
 */
public class BackgroundJob {

    private BackgroundJob() {
    }

    private static JobScheduler jobScheduler;

    /**
     * Creates a new fire-and-forget job based on a given lambda.
     * <h5>An example:</h5>
     * <pre>{@code
     *            MyService service = new MyService();
     *            BackgroundJob.enqueue(() -> service.doWork());
     *       }</pre>
     *
     * @param job the lambda which defines the fire-and-forget job
     * @return the id of the job
     */
    public static UUID enqueue(JobLambda job) {
        verifyJobScheduler();
        return jobScheduler.enqueue(job);
    }

    /**
     * Creates new fire-and-forget jobs for each item in the input stream using the lambda passed as {@code jobFromStream}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      Stream<UUID> workStream = getWorkStream();
     *      BackgroundJob.enqueue(workStream, (uuid) -> service.doWork(uuid));
     * }</pre>
     *
     * @param input         the stream of items for which to create fire-and-forget jobs
     * @param jobFromStream the lambda which defines the fire-and-forget job to create for each item in the {@code input}
     */
    public static <TItem> void enqueue(Stream<TItem> input, JobLambdaFromStream<TItem> jobFromStream) {
        verifyJobScheduler();
        jobScheduler.enqueue(input, jobFromStream);
    }

    /**
     * Creates a new fire-and-forget job based on a given lambda. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *            BackgroundJob.<MyService>enqueue(x -> x.doWork());
     *       }</pre>
     *
     * @param iocJob the lambda which defines the fire-and-forget job
     * @return the id of the job
     */
    public static <TService> UUID enqueue(IocJobLambda<TService> iocJob) {
        verifyJobScheduler();
        return jobScheduler.enqueue(iocJob);
    }

    /**
     * Creates new fire-and-forget jobs for each item in the input stream using the lambda passed as {@code jobFromStream}. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      Stream<UUID> workStream = getWorkStream();
     *      BackgroundJob.<MyService, UUID>enqueue(workStream, (x, uuid) -> x.doWork(uuid));
     * }</pre>
     *
     * @param input            the stream of items for which to create fire-and-forget jobs
     * @param iocJobFromStream the lambda which defines the fire-and-forget job to create for each item in the {@code input}
     */
    public static <TService, TItem> void enqueue(Stream<TItem> input, IocJobLambdaFromStream<TService, TItem> iocJobFromStream) {
        verifyJobScheduler();
        jobScheduler.enqueue(input, iocJobFromStream);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.schedule(() -> service.doWork(), ZonedDateTime.now().plusHours(5));
     * }</pre>
     *
     * @param job           the lambda which defines the fire-and-forget job
     * @param zonedDateTime The moment in time at which the job will be enqueued.
     */
    public static UUID schedule(JobLambda job, ZonedDateTime zonedDateTime) {
        verifyJobScheduler();
        return jobScheduler.schedule(job, zonedDateTime);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>schedule(x -> x.doWork(), ZonedDateTime.now().plusHours(5));
     * }</pre>
     *
     * @param iocJob        the lambda which defines the fire-and-forget job
     * @param zonedDateTime The moment in time at which the job will be enqueued.
     */
    public static <TService> UUID schedule(IocJobLambda<TService> iocJob, ZonedDateTime zonedDateTime) {
        verifyJobScheduler();
        return jobScheduler.schedule(iocJob, zonedDateTime);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.schedule(() -> service.doWork(), OffsetDateTime.now().plusHours(5));
     * }</pre>
     *
     * @param job            the lambda which defines the fire-and-forget job
     * @param offsetDateTime The moment in time at which the job will be enqueued.
     */
    public static UUID schedule(JobLambda job, OffsetDateTime offsetDateTime) {
        verifyJobScheduler();
        return jobScheduler.schedule(job, offsetDateTime);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>schedule(x -> x.doWork(), OffsetDateTime.now().plusHours(5));
     * }</pre>
     *
     * @param iocJob         the lambda which defines the fire-and-forget job
     * @param offsetDateTime The moment in time at which the job will be enqueued.
     */
    public static <TService> UUID schedule(IocJobLambda<TService> iocJob, OffsetDateTime offsetDateTime) {
        verifyJobScheduler();
        return jobScheduler.schedule(iocJob, offsetDateTime);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.schedule(() -> service.doWork(), LocalDateTime.now().plusHours(5));
     * }</pre>
     *
     * @param job           the lambda which defines the fire-and-forget job
     * @param localDateTime The moment in time at which the job will be enqueued. It will use the systemDefault ZoneId to transform it to an UTC Instant
     */
    public static UUID schedule(JobLambda job, LocalDateTime localDateTime) {
        verifyJobScheduler();
        return jobScheduler.schedule(job, localDateTime);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>schedule(x -> x.doWork(), LocalDateTime.now().plusHours(5));
     * }</pre>
     *
     * @param iocJob        the lambda which defines the fire-and-forget job
     * @param localDateTime The moment in time at which the job will be enqueued. It will use the systemDefault ZoneId to transform it to an UTC Instant
     */
    public static <TService> UUID schedule(IocJobLambda<TService> iocJob, LocalDateTime localDateTime) {
        verifyJobScheduler();
        return jobScheduler.schedule(iocJob, localDateTime);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.schedule(() -> service.doWork(), Instant.now().plusHours(5));
     * }</pre>
     *
     * @param job     the lambda which defines the fire-and-forget job
     * @param instant The moment in time at which the job will be enqueued.
     */
    public static UUID schedule(JobLambda job, Instant instant) {
        verifyJobScheduler();
        return jobScheduler.schedule(job, instant);
    }

    /**
     * Creates a new fire-and-forget job based on the given lambda and schedules it to be enqueued at the given moment of time. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>schedule(x -> x.doWork(), Instant.now().plusHours(5));
     * }</pre>
     *
     * @param iocJob  the lambda which defines the fire-and-forget job
     * @param instant The moment in time at which the job will be enqueued.
     */
    public static <TService> UUID schedule(IocJobLambda<TService> iocJob, Instant instant) {
        verifyJobScheduler();
        return jobScheduler.schedule(iocJob, instant);
    }

    /**
     * Creates a new recurring job based on the given lambda and the given cron expression. The jobs will be scheduled using the systemDefault timezone.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.scheduleRecurringly(() -> service.doWork(), Cron.daily());
     * }</pre>
     *
     * @param job  the lambda which defines the fire-and-forget job
     * @param cron The cron expression defining when to run this recurring job
     * @return the id of this recurring job which can be used to alter or delete it
     * @see org.jobrunr.scheduling.cron.Cron
     */
    public static String scheduleRecurringly(JobLambda job, String cron) {
        verifyJobScheduler();
        return jobScheduler.scheduleRecurringly(job, cron);
    }

    /**
     * Creates a new recurring job based on the given lambda and the given cron expression. The IoC container will be used to resolve {@code MyService}. The jobs will be scheduled using the systemDefault timezone.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>scheduleRecurringly(x -> x.doWork(), Cron.daily());
     * }</pre>
     *
     * @param iocJob the lambda which defines the fire-and-forget job
     * @param cron   The cron expression defining when to run this recurring job
     * @return the id of this recurring job which can be used to alter or delete it
     * @see org.jobrunr.scheduling.cron.Cron
     */
    public static <TService> String scheduleRecurringly(IocJobLambda<TService> iocJob, String cron) {
        verifyJobScheduler();
        return jobScheduler.scheduleRecurringly(iocJob, cron);
    }

    /**
     * Creates a new recurring job based on the given id, lambda and cron expression. The jobs will be scheduled using the systemDefault timezone
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.scheduleRecurringly("my-recurring-job", () -> service.doWork(), Cron.daily());
     * }</pre>
     *
     * @param id   the id of this recurring job which can be used to alter or delete it
     * @param job  the lambda which defines the fire-and-forget job
     * @param cron The cron expression defining when to run this recurring job
     * @return the id of this recurring job which can be used to alter or delete it
     * @see org.jobrunr.scheduling.cron.Cron
     */
    public static String scheduleRecurringly(String id, JobLambda job, String cron) {
        verifyJobScheduler();
        return jobScheduler.scheduleRecurringly(id, job, cron);
    }

    /**
     * Creates a new recurring job based on the given id, lambda and cron expression. The IoC container will be used to resolve {@code MyService}. The jobs will be scheduled using the systemDefault timezone
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>scheduleRecurringly("my-recurring-job", x -> x.doWork(), Cron.daily());
     * }</pre>
     *
     * @param id     the id of this recurring job which can be used to alter or delete it
     * @param iocJob the lambda which defines the fire-and-forget job
     * @param cron   The cron expression defining when to run this recurring job
     * @return the id of this recurring job which can be used to alter or delete it
     * @see org.jobrunr.scheduling.cron.Cron
     */
    public static <TService> String scheduleRecurringly(String id, IocJobLambda<TService> iocJob, String cron) {
        verifyJobScheduler();
        return jobScheduler.scheduleRecurringly(id, iocJob, cron);
    }

    /**
     * Creates a new recurring job based on the given id, lambda, cron expression and {@code ZoneId}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      MyService service = new MyService();
     *      BackgroundJob.scheduleRecurringly("my-recurring-job", () -> service.doWork(), Cron.daily(), ZoneId.of("Europe/Brussels"));
     * }</pre>
     *
     * @param id     the id of this recurring job which can be used to alter or delete it
     * @param job    the lambda which defines the fire-and-forget job
     * @param cron   The cron expression defining when to run this recurring job
     * @param zoneId The zoneId (timezone) of when to run this recurring job
     * @return the id of this recurring job which can be used to alter or delete it
     * @see org.jobrunr.scheduling.cron.Cron
     */
    public static String scheduleRecurringly(String id, JobLambda job, String cron, ZoneId zoneId) {
        verifyJobScheduler();
        return jobScheduler.scheduleRecurringly(id, job, cron, zoneId);
    }

    /**
     * Creates a new recurring job based on the given id, lambda, cron expression and {@code ZoneId}. The IoC container will be used to resolve {@code MyService}.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.<MyService>scheduleRecurringly("my-recurring-job", x -> x.doWork(), Cron.daily(), ZoneId.of("Europe/Brussels"));
     * }</pre>
     *
     * @param id     the id of this recurring job which can be used to alter or delete it
     * @param iocJob the lambda which defines the fire-and-forget job
     * @param cron   The cron expression defining when to run this recurring job
     * @param zoneId The zoneId (timezone) of when to run this recurring job
     * @return the id of this recurring job which can be used to alter or delete it
     * @see org.jobrunr.scheduling.cron.Cron
     */
    public static <TService> String scheduleRecurringly(String id, IocJobLambda<TService> iocJob, String cron, ZoneId zoneId) {
        verifyJobScheduler();
        return jobScheduler.scheduleRecurringly(id, iocJob, cron, zoneId);
    }

    /**
     * Deletes the recurring job based on the given id.
     * <h5>An example:</h5>
     * <pre>{@code
     *      BackgroundJob.deleteRecurringly("my-recurring-job"));
     * }</pre>
     *
     * @param id the id of the recurring job to delete
     */
    public static void deleteRecurringly(String id) {
        verifyJobScheduler();
        jobScheduler.deleteRecurringly(id);
    }

    private static void verifyJobScheduler() {
        if (jobScheduler != null) return;
        throw new IllegalStateException("The JobScheduler has not been initialized. Use the fluent JobRunr.configure() API to setup JobRunr,");
    }

    public static void setJobScheduler(JobScheduler jobScheduler) {
        BackgroundJob.jobScheduler = jobScheduler;
    }
}
