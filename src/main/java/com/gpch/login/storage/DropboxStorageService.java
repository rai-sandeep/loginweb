package com.gpch.login.storage;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.dropbox.core.DbxDownloader;
import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CreateFolderErrorException;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.ListFolderErrorException;
import com.dropbox.core.v2.files.ListFolderResult;
import com.dropbox.core.v2.files.Metadata;
import com.dropbox.core.v2.files.UploadErrorException;

@Service
public class DropboxStorageService  implements StorageService {
	
	private static final String ACCESS_TOKEN = "IE-mOc5CauoAAAAAAAAVUjdbwg7n4fXfz50JtryxI_pU50vnzyJFb6J4J9cEkqvy";
	private DbxClientV2 client;
	
	 private final String rootLocation;

	    @Autowired
	    public DropboxStorageService(StorageProperties properties) {
	        this.rootLocation = properties.getLocation();
	    }

	@Override
	public void init() {
		// Create Dropbox client
        DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
        client = new DbxClientV2(config, ACCESS_TOKEN);
        try {
			Files.createDirectories(Paths.get(rootLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public void store(MultipartFile file, String userEmailId) {

        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream in = file.getInputStream()) {
            	client.files().uploadBuilder("/"+userEmailId+"/"+file.getOriginalFilename())
			    .uploadAndFinish(in);
            }
        }
        catch (UploadErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	@Override
	public Stream<Path> loadAll(String userEmailId) {
		
		List<Path> pathList = new ArrayList<Path>();
		
		// Get files and folder metadata from Dropbox root directory
		ListFolderResult result = null;
		try {
			result = client.files().listFolder("/"+userEmailId);
		} catch (ListFolderErrorException e) {
			if (e.getMessage().contains("not_found")) {
				createFolder(userEmailId);
			}
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (result!=null) {
			while (true) {
			    for (Metadata metadata : result.getEntries()) {
			        System.out.println(metadata.getPathLower());
			        pathList.add(Paths.get(metadata.getPathLower()));
			    }

			    if (!result.getHasMore()) {
			        break;
			    }
			}
		}
		
		
		return pathList.stream();
		
	}

	@Override
	public Path load(String filename) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Resource loadAsResource(String filename, String userEmailId) {

		Resource resource = null;
		
		try{
			DbxDownloader<FileMetadata> file = client.files().download("/"+userEmailId+"/"+filename);
			if (file!=null) {
				/*ByteArrayOutputStream bos = new ByteArrayOutputStream();
				file.download(bos);
				resource = new ByteArrayResource(bos.toByteArray());*/
				FileOutputStream fos = new FileOutputStream(rootLocation+"/"+filename);
				file.download(fos);
				fos.close();
				resource = new UrlResource("file:"+rootLocation+"/"+filename);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return resource;
	}

	@Override
	public void deleteAll() {
		try {
			FileSystemUtils.deleteRecursively(Paths.get(rootLocation));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void createFolder(String userEmailId) {
		try {
			client.files().createFolderV2("/"+userEmailId);
		} catch (CreateFolderErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DbxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
