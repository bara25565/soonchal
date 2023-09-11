package hello.hellospring.controller;

public class requestDTO {
    public static class WorkerChangeRequest {
        private String changeDate;
        private String fromWorker;
        private String toWorker;

        public String getChangeDate() {
            return changeDate;
        }

        public void setChangeDate(String changeDate) {
            this.changeDate = changeDate;
        }

        public String getFromWorker() {
            return fromWorker;
        }

        public void setFromWorker(String getFromWorker) {
            this.fromWorker = getFromWorker;
        }

        public String getToWorker() {
            return toWorker;
        }

        public void setToWorker(String getToWorker) {
            this.toWorker = getToWorker;
        }
    }
}
