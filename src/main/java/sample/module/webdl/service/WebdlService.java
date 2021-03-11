package sample.module.webdl.service;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sample.module.webdl.model.*;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebdlService {
    private final String tempFolder="ytemp";

    private List<YVideo> videoList;
    private List<YAudio> audioList;


    public List<YVideo> getVideoList() {
        return videoList;
    }

    public List<YAudio> getAudioList() {
        return audioList;
    }


    public WebdlService(){
        System.out.println("객체 생성");
        videoList=new ArrayList<YVideo>();
        audioList=new ArrayList<YAudio>();
    }

    private FileExtensionType getFileExtension(String mimeStr){
        if(mimeStr.contains("mp4"))
        {
            return FileExtensionType.mp4;
        }
        else if(mimeStr.contains("webm"))
        {
            return FileExtensionType.webm;
        }
        else if(mimeStr.contains("weba")){
            return FileExtensionType.weba;
        }
        else
        {
            return FileExtensionType.NoExtension;
        }
    }
    public List<String> getYVideoArticleList(String address)

    {
        List<String> list=new ArrayList<String>();
        try {
            Document doc = Jsoup.connect(address).get();
            Elements atag = doc.getElementsByTag("a");

            System.out.println(atag.toString());
            String hrefTag=atag.attr("href");
            System.out.println(hrefTag);
            list.add(hrefTag);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }


    private boolean setList(String htmlTxt,String youtubeId){
        System.out.println("setList");
        String name=getTitle(htmlTxt);
        int len=htmlTxt.length();
        int start =htmlTxt.indexOf("var ytInitialPlayerResponse");
        String cutfront=htmlTxt.substring(start,len);
        int end=cutfront.indexOf(";</script>");
        String cutend=cutfront.substring(0,end);
        String jsonTxt=cutend.substring(cutend.indexOf("{"),cutend.length());
        //System.out.println(jsonTxt);
        try {
            JSONObject j1= (JSONObject)new JSONParser().parse(jsonTxt);
            System.out.println(j1);
            JSONObject j2=(JSONObject)j1.get("streamingData");
            JSONArray adaptiveFormats=(JSONArray)j2.get("adaptiveFormats");
            //전체 내역 출력
            System.out.println(adaptiveFormats.toString());
            JSONObject object=null;
            for(int i=0;i<adaptiveFormats.size();i++)
            {
                object=(JSONObject)adaptiveFormats.get(i);
                String mimeType=object.get("mimeType").toString();

                if(mimeType.contains("video"))
                {
                    YVideo video=new YVideo();
                    String qualityLabel=object.get("qualityLabel").toString();
                    String fps=object.get("fps").toString();
                    String downloadUrl="";
                    //한 번에 url을 찾지 못하였을경우
                    if(object.get("url")==null)
                    {
                        downloadUrl=decodeURL(
                                ((JSONObject)adaptiveFormats.get(i))
                                        .get("signatureCipher").toString());
                        int lastindex=downloadUrl.indexOf("https");
                        String step1=downloadUrl.substring(lastindex,downloadUrl.length());

                        lastindex=step1.indexOf("\"");
                        System.out.println(lastindex);
                        downloadUrl=decodeURL(step1.substring(0,lastindex));
                        System.out.println(downloadUrl);
                    }else
                    {
                        downloadUrl=decodeURL(object.get("url").toString());
                    }

                    JSONObject contentJson=null;
                    int contentsSize=0;
                    if(((JSONObject)adaptiveFormats.get(i)).get("contentLength")!=null)
                    {
                        contentsSize=Integer.parseInt(((JSONObject)adaptiveFormats.get(i)).get("contentLength").toString());
                    }

//                    System.out.println("mimeType : "+mimeType);
//                    System.out.println("qualityLabel : "+qualityLabel);
//                    System.out.println("fps : "+fps);
//                    System.out.println("averageBitrate : "+averageBitrate);
                    video.setName(name);
                    video.setYoutubeId(youtubeId);
                    video.setMimeType(mimeType);
                    video.setFileExtensionType(getFileExtension(mimeType));
                    video.setQualityLabel(qualityLabel);
                    video.setFps(fps);

                    video.setDownloadUrl(downloadUrl);
                    video.setContentSize(contentsSize);


                    videoList.add(video);
                }
                else if(mimeType.contains("audio"))
                {
                    object=(JSONObject)adaptiveFormats.get(i);
                    YAudio audio=new YAudio();
                    String audioChannels=object.get("audioChannels").toString();
                    String audioSampleRate=object.get("audioSampleRate").toString();
                    String downloadUrl="";
                    if(object.get("url")==null)
                    {
                        downloadUrl=decodeURL(
                                ((JSONObject)adaptiveFormats.get(i))
                                        .get("signatureCipher").toString());
                        int lastindex=downloadUrl.lastIndexOf("url=h");
                        downloadUrl=decodeURL(downloadUrl.substring(lastindex,downloadUrl.length()).replace("url=",""));
                        System.out.println(downloadUrl);
                    }else
                    {
                        downloadUrl=decodeURL(object.get("url").toString());
                    }

                    JSONObject contentJson=null;
                    int contentsSize=0;
                    if(((JSONObject)adaptiveFormats.get(i)).get("contentLength")!=null)
                    {
                        contentsSize=Integer.parseInt(((JSONObject)adaptiveFormats.get(i)).get("contentLength").toString());
                    }

//                    System.out.println("mimeType : "+mimeType);
//                    System.out.println("audioSampleRate : "+audioSampleRate);
//                    System.out.println("audioChannels : "+audioChannels);
//                    System.out.println("averageBitrate : "+averageBitrate);
                    audio.setName(name);
                    audio.setYoutubeId(youtubeId);
                    audio.setMimeType(mimeType);
                    audio.setFileExtensionType(getFileExtension(mimeType));
                    audio.setAudioChannels(audioChannels);
                    audio.setAudioSampleRate(audioSampleRate);
                    audio.setDownloadUrl(downloadUrl);
                    audio.setContentSize(contentsSize);
                    audioList.add(audio);
                }




                //System.out.println(i+". "+decodeURL(((JSONObject)formats.get(i)).get("mimeType").toString()));
            }


        } catch (ParseException e) {

            e.printStackTrace();
            return false;
        }
        return true;
    }
    private String getFileNameCheck(String name)
    {
        String nameDecode=name;


        try{
        nameDecode=URLDecoder.decode(name,"UTF-8");

        } catch (UnsupportedEncodingException e) {

        }
        return nameDecode.trim().replace("\\","")
                .replace("//","")
                .replace(":","")
                .replace("*","")
                .replace("\"","")
                .replace("<","")
                .replace(">","")
                .replace("|","")
                .replace("\'","")
                .replace("&quot;","")
                .replace("&#39;","");
    }

    private String getTitle(String htmlTxt)
    {
        System.out.println("getTitle");
        int start=htmlTxt.indexOf("<title>");
        int end=htmlTxt.indexOf("</title>");
        System.out.println(htmlTxt.substring(start,end).replace("<title>",""));
        return getFileNameCheck(htmlTxt.substring(start,end).replace("<title>",""));
    }
    private String decodeURL(String url)
    {
        System.out.println("decodeURL");
        String res="";
        try {
            res= URLDecoder.decode(url.replace("\\u0026","&"),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return res;
    }



    public String getVideoId(String videoUrl) {
        System.out.println("getVideioId");
        String expression = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%\u200C\u200B2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*";
        if (videoUrl == null || videoUrl.trim().length() <= 0){
            return null;
        }
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(videoUrl);
        try {
            if (matcher.find())
                return matcher.group();
        } catch (ArrayIndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public boolean findListData(String address)
    {
        System.out.println("findListData");
        HttpURLConnection conn = null;
        String youtubeId=getVideoId(address);
        StringBuilder contents = new StringBuilder();
        try {
            conn = (HttpURLConnection)new URL("https://youtu.be/"+youtubeId).openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);

            InputStream is = conn.getInputStream();

            String enc = conn.getContentEncoding();

            if (enc == null) {
                Pattern p = Pattern.compile("charset=(.*)");
                Matcher m = p.matcher(conn.getHeaderField("Content-Type"));
                if (m.find()) {
                    enc = m.group(1);
                }
            }

            if (enc == null)
                enc = "UTF-8";

            BufferedReader br = new BufferedReader(new InputStreamReader(is, enc));

            String line = null;


            while ((line = br.readLine()) != null) {
                contents.append(line);
                contents.append("\n");

            }
        }catch(IOException e){
            return false;
        }

        return setList(contents.toString(),youtubeId);
    }
    public boolean YMediaDownloader(YMedia media, String path)
    {
        if(media.getVideo()!=null&&media.getAudio()!=null)
        {
            boolean videostate=VideoDownload(media.getVideo());
            boolean audiostate=AudioDownload(media.getAudio());
            if(videostate&&audiostate)
            {
                mergeFFmpeg(media.getVideo(),media.getAudio(),path);
            }
        }else if(media.getAudio()!=null)
        {
            FileDownload fileDownload=new FileDownload();
            fileDownload.setName(media.getAudio().getName());
            fileDownload.setContentSize(media.getAudio().getContentSize());
            fileDownload.setFileExtensionType(media.getAudio().getFileExtensionType());
            fileDownload.setUrl(media.getAudio().getDownloadUrl());
            fileDownload.setFile(new File(path));
            URLDownloader(fileDownload);
        }else if(media.getVideo()!=null)
        {
            FileDownload fileDownload=new FileDownload();
            fileDownload.setName(media.getVideo().getName());
            fileDownload.setContentSize(media.getVideo().getContentSize());
            fileDownload.setFileExtensionType(media.getVideo().getFileExtensionType());
            fileDownload.setUrl(media.getVideo().getDownloadUrl());
            fileDownload.setFile(new File(path));
            URLDownloader(fileDownload);
        }

        close();//리스트 정리

        return true;
    }

      public boolean URLDownloader(FileDownload fileDownload) {
          File file=fileDownload.getFile();
          if(!file.exists())file.mkdirs();
          BufferedInputStream bufferedInputStream=null;
          FileOutputStream fileOutputStream=null;
            try{

                bufferedInputStream = new BufferedInputStream(new URL(fileDownload.getUrl()).openStream());
                fileOutputStream = new FileOutputStream(file.getPath() +"/"+
                        fileDownload.getName() + "." + fileDownload.getFileExtensionType());
                byte dataBuffer[] = new byte[1024];
                int bytesRead=0;
                double current = 0;
                double origin = fileDownload.getContentSize();
                double processing = 0;

                while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1) {
                    current += bytesRead;
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    if (processing != Math.round((current / origin) * 100)) {
                        processing = Math.round((current / origin) * 100);
                        System.out.println("downloading : " + processing);
                    }


                }




            }catch(IOException e)
            {
                close(bufferedInputStream,fileOutputStream);
                return false;
            }finally{
                close(bufferedInputStream,fileOutputStream);
            }




        return true;
      }

      private boolean mergeFFmpeg(YVideo video, YAudio audio,String path)
      {

          File file=new File(path);
          if(file.exists()){
              try {

                  String[] ffmpeg={"ffmpeg",
                          "-y",
                          "-i",
                          "\"./"+tempFolder+"/test.vtemp\"",
                          "-i",
                          "\"./"+tempFolder+"/test.atemp\"",
                          "-c",

                          "copy",
                          "\""+file.getPath()+video.getName()+".mp4\""
                            };
                  ProcessBuilder processBuilder=new ProcessBuilder(ffmpeg);
                 processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);


                  //Runtime runtime=Runtime.getRuntime();
                  Process process=  processBuilder.inheritIO().start();
                  try {
                      process.waitFor();
                  } catch (InterruptedException e) {
                      e.printStackTrace();
                  }

//                  long time=System.currentTimeMillis();
//                  int counter=0;
//                  while(process.isAlive())
//                  {
//                    if(time+1000<System.currentTimeMillis())
//                    {
//
//                        System.out.println("Encoding.."+(++counter));
//                        time=System.currentTimeMillis();
//                        if(counter>3)
//                        {
//                            process.destroy();
//                            process=null;
//                            process=processBuilder.start();
//                            //process=runtime.exec(ffmpeg);
//                            counter=0;
//                        }
//                    }
//
//                  }
//                  process.getInputStream().close();
//                  process.getOutputStream().close();
//                  process.getErrorStream().close();

                  process.destroy();
//                  process=null;


                  callFileDeleter(new File("./"+tempFolder));
                  //    Runtime.getRuntime().exec(unixrm);



              } catch (IOException e) {
                  e.printStackTrace();
                  return false;
              }
          }



          return true;
      }
      private void callFileDeleter(File file)
      {

          File[] files=file.listFiles();
          for(int i=0;i<files.length;i++)
          {
              if(files[i].isDirectory())
              {
                  callFileDeleter(file);
              }else
              {
                  files[i].delete();
              }

          }
          file.delete();
      }


      private boolean  AudioDownload(YAudio audio)
      {
          File file=new File("./"+tempFolder);
          if(!file.exists())file.mkdirs();
          System.out.println("AudioDownloading");
          BufferedInputStream bufferedInputStream=null;
          FileOutputStream fileOutputStream=null;
          try {
              bufferedInputStream= new BufferedInputStream(new URL(audio.getDownloadUrl()).openStream());
              fileOutputStream = new FileOutputStream(
              file.getPath()+"/"+
                      "test.atemp");


              byte dataBuffer[] = new byte[1024];
              int bytesRead;
              double current = 0;
              double origin = audio.getContentSize();
              double processing = 0;

              while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1) {
                  current += bytesRead;
                  fileOutputStream.write(dataBuffer, 0, bytesRead);
                  if (processing != Math.round((current / origin) * 100)) {
                      processing = Math.round((current / origin) * 100);
                      System.out.println("audio downloading : " + processing);
                  }


              }

          } catch (IOException e) {
              System.out.println("URLDownload: Video Output Error");
              close(bufferedInputStream,fileOutputStream);
              return false;
          } // handle exception }
          finally{
              close(bufferedInputStream,fileOutputStream);

          }
          return true;
      }
      private void close(BufferedInputStream bufferedInputStream,FileOutputStream fileOutputStream){
          if(bufferedInputStream!=null)
          {
              try {
                  bufferedInputStream.close();
                  bufferedInputStream=null;
              } catch (IOException e) {
                  //이미 닫혀서 오류
              }
          }
          if(fileOutputStream!=null)
          {
              try{
                  fileOutputStream.close();
                  fileOutputStream=null;
              }catch(IOException e)
              {
                  //이미 닫혀서 오류
              }
          }
      }
    private boolean VideoDownload(YVideo video){
            File file=new File("./"+tempFolder);
            if(!file.exists())file.mkdirs();
            System.out.println("VideoDownloading");
            BufferedInputStream bufferedInputStream=null;
            FileOutputStream fileOutputStream=null;
            try {
                bufferedInputStream = new BufferedInputStream(new URL(video.getDownloadUrl()).openStream());

                fileOutputStream  = new FileOutputStream(
                    file.getPath()+"/"+
                        "test.vtemp");


                byte dataBuffer[] = new byte[1024];
                int bytesRead;
                double current = 0;
                double origin = video.getContentSize();
                double processing = 0;

                while ((bytesRead = bufferedInputStream.read(dataBuffer, 0, 1024)) != -1) {
                    current += bytesRead;
                    fileOutputStream.write(dataBuffer, 0, bytesRead);
                    if (processing != Math.round((current / origin) * 100)) {
                        processing = Math.round((current / origin) * 100);
                        System.out.println("video downloading : " + processing);
                    }


                }

            } catch (IOException e) {
                System.out.println("URLDownload: Video Output Error");
                close(bufferedInputStream,fileOutputStream);
                return false;
            } // handle exception }
            finally{
                close(bufferedInputStream,fileOutputStream);
            }

        return true;
    }
    private void close()
    {
        videoList.clear();
        audioList.clear();
    }

}
