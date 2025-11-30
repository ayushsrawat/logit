- Logit backoff pressure on fluentBit when tried 5 filestream of the kafka_logs copies
- Let's see if the introduction of Chronicle-Queue will solve this issue!?
- CrashTest.java however wasn't able to break the logit under no-chronicle queuing.

```bash

[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978339000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978404000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01.978454000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978487000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978512000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978534000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978554000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1/lib/monkey/mk_core/mk_event_kqueue.c:198
[2025/11/29 12:20:01.978575000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
[2025/11/29 12:20:01] [  Error] open: Too many open files, errno=24 at /private/tmp/fluent-bit-20251009-6629-g0dt1o/fluent-bit-4.1.1[2025/11/29 12:20:01.978596000] [error] [sched] a 'retry request' could not be scheduled. the system might be running out of memory or file descriptors. The scheduler will do a retry later.
/lib/monkey/mk_core/mk_event_kqueue.c:198
```

- Even after implementing Chronicle Queue in logit this stress test fails
- These errors suggest me this is a system or fluent bit issue rather than logit