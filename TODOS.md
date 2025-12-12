# LOGIT
A small, production-grade log indexing & search microservice.

## TODOS
- [X] add multiple sources of logs with different regex and then handle them in logit
- [X] the above will quite increase the resiliency and the shape of the project, because from the birth it know how to handle diverse data 
- [X] consider adding lightweight queueing service like rabbitmq to asynchronously handle the log indexing
- [ ] UI for the same
  - [ ] web sockets for live log monitoring
  - [ ] dashboards with various ag-charts
- [ ] can we think about creating a generator for the fluent-bit.yaml?
- [ ] web socket channels for different indexes for live console view
- [ ] minimal database (like sqlite) for user authentications
- [X] different analyzers for Indexing and Searching so that we can impl stemming on/off feature
- [X] consider testing log file rotation for fluent bit 
- [ ] Authentication key when fluent bit send http log events?
- [X] scheduler to delete chronicle queue data
- [X] how to have pointer in chronicle queue for consumer to know which files are read?
- [X] if we start the server, the consumer starts reading the same logs! fix this asap
- [ ] you are searching all the logs even though you just have to give topN so need to fix this for performance reasons
- [ ] include the notion of time in the search query