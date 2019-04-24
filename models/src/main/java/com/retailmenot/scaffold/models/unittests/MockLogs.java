package com.retailmenot.scaffold.models.unittests;

import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.Beta;
import org.openqa.selenium.logging.LogEntries;
import org.openqa.selenium.logging.LogEntry;
import org.openqa.selenium.logging.Logs;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Beta
@Slf4j
public class MockLogs implements Logs {

    private LogEntries logEntries;

    @Override
    public LogEntries get(String logType) {
        return logEntries;
    }

    @Override
    public Set<String> getAvailableLogTypes() {
        return Collections.emptySet();
    }

    public void set(List<LogEntry> logEntryList) {
        logEntries = new LogEntries(logEntryList);
    }
}
