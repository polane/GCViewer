package com.tagtraum.perf.gcviewer;

import com.tagtraum.perf.gcviewer.exp.DataWriter;
import com.tagtraum.perf.gcviewer.exp.DataWriterType;
import com.tagtraum.perf.gcviewer.exp.impl.DataWriterFactory;
import com.tagtraum.perf.gcviewer.imp.DataReaderException;
import com.tagtraum.perf.gcviewer.imp.DataReaderFacade;
import com.tagtraum.perf.gcviewer.model.GCModel;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GCViewer {
    private static final Logger LOGGER = Logger.getLogger(GCViewer.class.getName());

	public static void main(final String[] args) {
        if (args.length > 3) {
            usage();
        }
        else if (args.length >= 3) {
        	final String gcfile = args[0];
        	final String summaryFilePath = args[1];
        	final String detailsFilePath = args[2];
            final String chartFilePath = args.length == 4 ? args[3] : null;

            //export summary:
            try {
                export(gcfile, summaryFilePath, detailsFilePath, chartFilePath);
                System.exit(0);
            }
            catch(Exception e) {
                LOGGER.log(Level.SEVERE, "Error during report generation", e);
                System.exit(-1);
            }
        }
        else {
        	GCViewerGui.start(args.length == 1 ? args[0] : null);
        }
    }

    private static void export(String gcFilename, String summaryFilePath, String detailsFilePath, String chartFilePath)
            throws IOException, DataReaderException {
        DataReaderFacade dataReaderFacade = new DataReaderFacade();
        GCModel model = dataReaderFacade.loadModel(gcFilename, false, null);

        exportSummary(model, summaryFilePath);
        exportDetails(model, detailsFilePath);
        if (chartFilePath != null)
            renderChart(model, chartFilePath);
    }

	private static void exportSummary(GCModel model, String summaryFilePath) throws IOException {
        try (DataWriter summaryWriter = DataWriterFactory.getDataWriter(new File(summaryFilePath), DataWriterType.SUMMARY)) {
            summaryWriter.write(model);
        }
    }
	
	private static void exportDetails(GCModel model, String detailsFilePath) throws IOException {
        try (DataWriter datailsyWriter = DataWriterFactory.getDataWriter(new File(detailsFilePath), DataWriterType.CSV)) {
        	datailsyWriter.write(model);
        }
    }

    private static void renderChart(GCModel model, String chartFilePath) throws IOException {
        SimpleChartRenderer renderer = new SimpleChartRenderer();
        renderer.render(model, chartFilePath);
    }

	private static void usage() {
		System.out.println("Welcome to GCViewer with cmdline");
        System.out.println("java -jar gcviewer.jar [<gc-log-file|url>] -> opens gui and loads given file");
        System.out.println("java -jar gcviewer.jar [<gc-log-file>] [<summary-export.csv> <details-export.csv>] "+
        		"-> cmdline: writes reports to <summary-export.csv> and details-export.csv");
        System.out.println("java -jar gcviewer.jar [<gc-log-file>] [<summary-export.csv> <details-export.csv>] [<chart.png>] " +
                "-> cmdline: writes reports to <summary-export.csv> and details-export.csv and renders gc chart to <chart.png>");
    }

}
