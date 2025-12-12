# Logit

**A lightweight, production-grade log indexing & search microservice.**

Logit is a custom-built solution designed to provide powerful log search capabilities without the operational complexity and resource overhead of heavy distributed systems. It combines the raw power of **Apache Lucene** for indexing with **Chronicle Queue** for ultra-low latency ingestion.

## Why Logit?

In the world of log observability, the **ELK Stack (Elasticsearch, Logstash, Kibana)** is the industry giant. However, for many smaller projects or single-server deployments, ELK is overkill:

*   **Resource Heavy**: Elasticsearch is a memory hog, often requiring gigabytes of RAM just to idle.
*   **Complexity**: Managing a distributed cluster when you only have one machine adds unnecessary operational burden.
*   **"Magic"**: Sometimes you just want to index strings and search them without dealing with complex sharding, replication, and node coordination.

**Logit fills this gap.** It runs as a single, efficient Java process. It gives you the full text search capabilities of Lucene (which Elasticsearch is built on) but stripped down to exactly what you need: **ingesting, indexing, and finding your logs.**

## Features

*   *** Lightweight**: Minimal memory footprint compared to ELK.
*   *** High Performance**: Built on **Java 25** and **Spring Boot 3.5**.
*   *** Direct Lucene**: Uses Apache Lucene 10.3 directly for optimized search operations.
*   *** Durable Ingestion**: Uses **Chronicle Queue** to buffer logs, ensuring no data loss even during high traffic bursts.
*   *** Universal Ingest**: Compatible with **Fluent Bit** (and anything else that can send JSON over HTTP).
*   *** Modern UI**: Clean, responsive frontend built with **Vite** and **Vanilla CSS**.

## Quick Start

### Prerequisites
*   Java 25+
*   Node.js (for the UI)

### 1. Start the Backend
The backend serves the API for ingestion and search.
```bash
./gradlew bootRun
```

### 2. Start the UI
The frontend provides a clean interface to search and filter your logs.
```bash
cd ui
npm install
npm run dev
```

### 3. Start Log Ingestion
We provide a pre-configured Fluent Bit setup to ship logs to Logit.
```bash
cd fluentbit
./start.sh
```
*(Ensure you have Fluent Bit installed or use the provided wrapper if applicable)*

---
*Built with ❤️ by Ayush Rawat*
