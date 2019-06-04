package data.collection;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import data.ingestion.KafkaBroker;
import data.mapping.YouTubeWrapper;

public class YouTubeCollector {
	// search video by keyword
		public static List<String> collectVideoByKeyword() {
			String keyword = "Obama";
			keyword = keyword.replace(" ", "+");
			List<String> listVideoIds = new ArrayList();
			
			String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&order=rating&q="
					+ keyword + "&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

			Document doc = null;
			try {
				doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true)
						.get();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String getJson = doc.text();
			System.out.println(" doc.text***" + getJson);
			JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
					.nextValue();
		System.out.println("jsonObject"+ jsonObject.toString());
			System.out.println("items" + jsonObject.getJSONArray("items"));
			JSONArray item = jsonObject.getJSONArray("items");
		
				createFile("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/data/video.json",
						((JSONObject)(item.iterator().next())).toString());
				//send each item.iterator to kafka 
					List<JSONObject> list = new ArrayList();
			
			
			for (int i = 0; i < item.length(); i++) {
				JSONObject obj = (JSONObject) (item.getJSONObject(i).getJSONObject(
						"snippet").remove("thumbnails"));
				list.add(item.getJSONObject(i).getJSONObject("snippet"));
				list.add(obj.getJSONObject("default"));
				list.add(obj.getJSONObject("high"));
				list.add(item.getJSONObject(i).getJSONObject("id"));
				System.out.println("id***"
						+ item.getJSONObject(i).getJSONObject("id").get("videoId"));
				listVideoIds.add(item.getJSONObject(i).getJSONObject("id")
						.get("videoId").toString());
			}
			/*for (int j=0; j<list1.size();j++){
			
			createFile("C:/Users/user/myWorkspace/Wrapper/resources/video.json",
					list1.get(j).toString());
			}*/
		/*	org.json.simple.JSONArray data = new org.json.simple.JSONArray();
			for (int j = 0; j < list.size(); j++) {
				data.add(list.get(j));
			}
			createFile("C:/Users/user/myWorkspace/Wrapper/resources/video.json",
					data.toJSONString());*/
			return listVideoIds;

		}

		public static void collectComment(List<String> listVideoIds) {
			for (int i = 0; i < listVideoIds.size(); i++) {
				// search video by topic
				String url = "https://www.googleapis.com/youtube/v3/commentThreads?part=snippet&maxResults=7&videoId="
						+ listVideoIds.get(i)
						+ "&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

				Document doc = null;
				try {
					doc = Jsoup.connect(url).ignoreHttpErrors(true)
							.timeout(10 * 1000).ignoreContentType(true).get();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String getJson = doc.text();
				System.out.println(" doc.text***" + getJson);
				JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
						.nextValue();
				// System.out.println("items"+ jsonObject.get("items"));
				System.out.println("items" + jsonObject.getJSONArray("items"));
				JSONArray item = jsonObject.getJSONArray("items");
				/*createFile("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/data/videoComment.json",
						((JSONObject)(item.iterator().next())).toString());*/
				
				for (int s=0;s<item.length();s++){
				JSONObject value=(JSONObject)(item.iterator().next());
				recreateFile("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/data/videoComment.json",
						((JSONObject)(item.iterator().next())).toString());
				try {
					YouTubeWrapper.mappComment();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				//send item to kafka 
				/*KafkaBroker broker =new KafkaBroker();
				broker.producerSubcription("YoutubeComment", value.toString());*/
				}
				List<JSONObject> list = new ArrayList();
				// list.add(item.getJSONObject(0).getJSONObject("id"));
				for (int k = 0; k < item.length(); k++) {
					list.add(item.getJSONObject(k).getJSONObject("snippet"));
				}
				/*org.json.simple.JSONArray data = new org.json.simple.JSONArray();
				for (int j = 0; j < list.size(); j++) {
					data.add(list.get(j));
				}
				createFile(
						"C:/Users/user/myWorkspace/Wrapper/resources/data/videoComment.json",
						data.toJSONString());*/

			}
		}

		public List<JSONObject> collectVideoByTopic() {
			String keyword = "SAMSUNG";
			keyword = keyword.replace(" ", "+");

			// search video by topic
			String url = "https://www.googleapis.com/youtube/v3/search?part=snippet &q="
					+ keyword
					+ "&type=video&videoCaption=closedCaption&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

			Document doc = null;
			try {
				doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true)
						.get();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String getJson = doc.text();
			System.out.println(" doc.text***" + getJson);
			JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
					.nextValue();
			// System.out.println("items"+ jsonObject.get("items"));
			System.out.println("items" + jsonObject.getJSONArray("items"));
			JSONArray item = jsonObject.getJSONArray("items");

			List<JSONObject> list = new ArrayList();
			// list.add(item.getJSONObject(0).getJSONObject("snippet"));
			JSONObject obj = (JSONObject) (item.getJSONObject(0).getJSONObject(
					"snippet").remove("thumbnails"));
			list.add(item.getJSONObject(0).getJSONObject("snippet"));
			list.add(obj.getJSONObject("default"));
			list.add(obj.getJSONObject("high"));

			list.add(item.getJSONObject(0).getJSONObject("id"));
			return list;
		}

		public List<JSONObject> getVideoRating() {
			String keyword = "SAMSUNG";
			keyword = keyword.replace(" ", "+");

			// search video by topic
			String url = "https://www.googleapis.com/youtube/v3/videos/getRating?id=dQA0otCJJxY&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

			Document doc = null;
			try {
				doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true)
						.get();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String getJson = doc.text();
			System.out.println(" doc.text***" + getJson);
			JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
					.nextValue();
			// System.out.println("items"+ jsonObject.get("items"));
			System.out.println("items" + jsonObject.getJSONArray("items"));
			JSONArray item = jsonObject.getJSONArray("items");

			List<JSONObject> list = new ArrayList();
			// list.add(item.getJSONObject(0).getJSONObject("snippet"));
			JSONObject obj = (JSONObject) (item.getJSONObject(0).getJSONObject(
					"snippet").remove("thumbnails"));
			list.add(item.getJSONObject(0).getJSONObject("snippet"));
			list.add(obj.getJSONObject("default"));
			list.add(obj.getJSONObject("high"));

			list.add(item.getJSONObject(0).getJSONObject("id"));
			return list;
		}

		public static List<String> collectChannel() {
			String keyword = "SAMSUNG";
			keyword = keyword.replace(" ", "+");
			List<String> listChannelIds = new ArrayList();

			// search video by topic
			String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&maxResults=1&q="
					+ keyword
					+ "&type=channel&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

			Document doc = null;
			try {
				doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true)
						.get();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String getJson = doc.text();
			System.out.println(" doc.text***" + getJson);
			JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
					.nextValue();
			// System.out.println("items"+ jsonObject.get("items"));
			System.out.println("items" + jsonObject.getJSONArray("items"));
			JSONArray item = jsonObject.getJSONArray("items");
			createFile("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/data/channel.json",
					((JSONObject)(item.iterator().next())).toString());
			
			List<JSONObject> list = new ArrayList();
			for (int i = 0; i < item.length(); i++) {
				JSONObject obj = (JSONObject) (item.getJSONObject(i).getJSONObject(
						"snippet").remove("thumbnails"));
				list.add(item.getJSONObject(i).getJSONObject("snippet"));
				list.add(obj.getJSONObject("default"));
				list.add(obj.getJSONObject("high"));
				list.add(item.getJSONObject(i).getJSONObject("id"));
				System.out.println("id***"
						+ item.getJSONObject(i).getJSONObject("id")
								.get("channelId"));
				listChannelIds.add(item.getJSONObject(i).getJSONObject("id")
						.get("channelId").toString());
			}
			
			
			return listChannelIds;

		}

		public static void collectChannelActivities(List<String> channelList) {

			for (int i = 0; i < channelList.size(); i++) {
				// search video by topic
				String url = "https://www.googleapis.com/youtube/v3/activities?part=snippet&channelId="
						+ channelList.get(i)
						+ "&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

				Document doc = null;
				try {
					doc = Jsoup.connect(url).ignoreHttpErrors(true)
							.timeout(10 * 1000).ignoreContentType(true).get();

				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String getJson = doc.text();
				System.out.println(" doc.text***" + getJson);
				JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
						.nextValue();
				// System.out.println("items"+ jsonObject.get("items"));
				System.out.println("items" + jsonObject.getJSONArray("items"));
				JSONArray item = jsonObject.getJSONArray("items");
				createFile("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/data/channelActivity.json",
						((JSONObject)(item.iterator().next())).toString());

				List<JSONObject> list = new ArrayList();
				// list.add(item.getJSONObject(0).getJSONObject("id"));
				for (int k = 0; k < item.length(); k++) {
					list.add(item.getJSONObject(k).getJSONObject("snippet"));
				}
				
				
			}
		}

		public static void collectPlayList() {
			String keyword = "SAMSUNG";
			keyword = keyword.replace(" ", "+");

			// search video by topic
			String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&q="
					+ keyword
					+ "&type=playlist&key=AIzaSyCVVbKcqVTkqma-U0ABmXjLs6P--qd_IEM";

			Document doc = null;
			try {
				doc = Jsoup.connect(url).timeout(10 * 1000).ignoreContentType(true)
						.get();

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String getJson = doc.text();
			System.out.println(" doc.text***" + getJson);
			JSONObject jsonObject = (JSONObject) new JSONTokener(getJson)
					.nextValue();

			System.out.println("items" + jsonObject.getJSONArray("items"));
			JSONArray item = jsonObject.getJSONArray("items");
			List<JSONObject> list = new ArrayList();
			for (int i = 0; i < item.length(); i++) {
				list.add(item.getJSONObject(0).getJSONObject("id"));
				list.add(item.getJSONObject(0).getJSONObject("snippet"));
			}
			org.json.simple.JSONArray data = new org.json.simple.JSONArray();
			for (int j = 0; j < list.size(); j++) {
				data.add(list.get(j));
			}
			createFile("/home/franz/eclipse_projects/SNOWLApplication/src/main/resources/data/PlayList.json",
					data.toJSONString());
		}

		public static void createFile(String filePath, String json) {
			try {
				String filename = filePath;
				FileWriter fw = new FileWriter(filename, true); // the true will
																// append the new
																// data
				fw.write(json);// appends the string to the file
				fw.close();
			} catch (IOException ioe) {
				System.err.println("IOException: " + ioe.getMessage());
			}
		}
		
public static void recreateFile (String path, String json){
	try{
    
    File file = new File(path);

    // If file doesn't exists, then create it
   
        file.createNewFile();
  
    FileWriter fw = new FileWriter(file.getAbsoluteFile());
    BufferedWriter bw = new BufferedWriter(fw);

    // Write in file
    bw.write(json);

    // Close connection
    bw.close();
}
catch(Exception e){
    System.out.println(e);
}
}

		public static void main(String[] args) {
//		List<String> listVideoIds= collectVideoByKeyword();
//	collectComment(listVideoIds);
		
	List<String> channelList=collectChannel();
		//collectChannelActivities(channelList);
			//collectPlayList();

		}

}
