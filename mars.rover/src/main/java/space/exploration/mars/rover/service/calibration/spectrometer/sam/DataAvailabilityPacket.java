package space.exploration.mars.rover.service.calibration.spectrometer.sam;

import space.exploration.mars.rover.sensor.SamSensor;

import space.exploration.mars.rover.utils.ServiceUtil;
import util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class DataAvailabilityPacket implements Serializable {
    private int          sol          = 0;
    private String       experimentId = null;
    private List<String> urls         = new ArrayList<>();

    public int getSol() {
        return sol;
    }

    public void setSol(int sol) {
        this.sol = sol;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }

    public void addUrl(String url) {
        urls.add(url);
    }

    public String getExperimentId() {
        return experimentId;
    }

    public void setExperimentId(String experimentId) {
        this.experimentId = experimentId;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Sol = ");
        stringBuilder.append(sol);
        stringBuilder.append(" ExperimentId = ");
        stringBuilder.append(experimentId);
        stringBuilder.append(" URLS = ");

        for (String url : urls) {
            stringBuilder.append(" " + url);
        }

        return stringBuilder.toString();
    }

    public List<File> downloadFiles() throws IOException {
        List<File> downloadedFiles = new ArrayList<>();

        for (String url : urls) {
            String targetUrl = SamSensor.SAM_DATA_BASE_URL + experimentId + "/level2/" + url;
            String path      = "dataArchives/SAM/" + sol;
            FileUtil.processDirectories(path);
            path += url;
            File csvFile = ServiceUtil.downloadCsv(targetUrl, path);
            if (csvFile != null) {
                downloadedFiles.add(csvFile);
            }
        }

        return downloadedFiles;
    }
}
