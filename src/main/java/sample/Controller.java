package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import sample.module.webdl.model.YAudio;
import sample.module.webdl.model.YMedia;
import sample.module.webdl.model.YVideo;
import sample.module.webdl.service.WebdlService;

import java.io.File;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.ResourceBundle;

public class Controller  implements Initializable {
    //webdl
    @FXML Button webdl_btnSelectPath;
    @FXML TextField webdl_pathtext;
    @FXML Button webdl_btnDownload;
    @FXML TextArea webdl_urlarea;
    @FXML CheckBox webdl_autoselect;

    private Stage primaryStage;


    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebdlController webdl=new WebdlController();
        webdl.init();

    }

    class WebdlController{
        WebdlService webdlService;
        Thread worker;
        double downProgress=0;
        double currentDownProgress=0;
        double fileProgress=0;
        double currentFileProgress=0;

        public WebdlController()
        {
            webdlService=new WebdlService();

        }
        private void init(){
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setInitialDirectory(new File("src"));
            webdl_btnSelectPath.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    File selectedDirectory = directoryChooser.showDialog(primaryStage);
                    String str=selectedDirectory.getPath();
                    if(str!=null)
                    {
                        webdl_pathtext.setText(selectedDirectory.getPath());
                        System.out.println(selectedDirectory.getAbsolutePath());
                    }

                }
            });

            webdl_btnDownload.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {

                    webdl_btnDownload.setDisable(true);
                    File path=new File(webdl_pathtext.getText());
                    if(!path.exists())path.mkdirs();
                    //경로가 있다면
                    if(path.exists()) {
                        String[] str = webdl_urlarea.getText().split("\n");
                        Queue<String> queue = new LinkedList<String>();
                        for (String data : str) {
                            if(data.length()>0)
                            {
                                queue.add(data.trim());
                            }


                        }
                        fileProgress = queue.size();
                        worker = new Thread(new downloader(queue, path.getPath()));
                        worker.start();



                    }else{
                        System.out.println("경로없음");
                    }






                }
            });

        }

        class downloader implements Runnable{
                private Queue<String> queue;
                private String path;
                public downloader(Queue<String> queue,String path)
                {
                    this.queue=queue;
                    this.path=path;
                }

                @Override
                public void run() {
                    try{
                        System.out.println("쓰레드실행");
                        //큐가 차있다면
                        while(!queue.isEmpty())
                        {
                            //큐에서 리스트를 구했다면
                            if(webdlService.findListData(queue.poll()))
                            {
                                    YVideo video=webdlService.getVideoList().get(0);
                                    YAudio audio=webdlService.getAudioList().get(0);
                                    YMedia media=new YMedia(video,audio);
                                    webdlService.YMediaDownloader(media,path);


                            };

                        }
                    }catch(Exception e)
                    {
                        e.printStackTrace();
                        System.out.println("쓰레드 오류");
                    }
                    finally {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                webdl_btnDownload.setDisable(false);
                            }
                        });
                    }



            };
        }
    }


}
