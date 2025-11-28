## LOGIT

## TODOS
- [X] add multiple sources of logs with different regex and then handle them in logit
- [X] the above will quite increase the resiliency and the shape of the project, because from the birth it know how to handle diverse data 
- [ ] consider adding lightweight queueing service like rabbitmq to asynchronously handle the log indexing
- [ ] UI for the same
  - [ ] web sockets for live log monitoring
  - [ ] dashboards with various ag-charts
- [ ] can we think about creating a generator for the fluent-bit.yaml?
- [ ] web socket channels for different indexes for live console view
- [ ] minimal database (like sqlite) for user authentications
- [X] different analyzers for Indexing and Searching so that we can impl stemming on/off feature
- [ ] consider testing log file rotation for fluent bit 