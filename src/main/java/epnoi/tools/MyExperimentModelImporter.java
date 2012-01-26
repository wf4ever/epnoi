package epnoi.tools;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import epnoi.model.File;
import epnoi.model.Model;
import epnoi.model.Pack;
import epnoi.model.Rating;
import epnoi.model.User;
import epnoi.model.Workflow;

public class MyExperimentModelImporter {

	public static void main(String[] args) {
		String FILE_NAME = "/packs.xml";
		//String FILE_NAME = "/lastImportedModel.xml";
		System.out.println("Extracting the myExperiment model");
		MyExperimentModelImporter importer = new MyExperimentModelImporter();
		importer.extractModel();
		importer.marshallToFile(FILE_NAME);

	}

	Model model = null;

	public MyExperimentModelImporter() {
		this.model = new Model();
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}

	public void extractModel() {

		this._extractWorkflows();
		
		this._extractUsers();

		this._extractRatings();
		this._extractFiles();
	
		this._extractPacks();
	}

	private void _extractUsers() {

		String usersQueryService = "http://rdf.myexperiment.org/sparql?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0Aselect+distinct+%3Fx+where+%7B%3Fx+rdf%3Atype+%3Chttp%3A%2F%2Frdf.myexperiment.org%2Fontologies%2Fbase%2FUser%3E%7D+&formatting=XML&softlimit=5;";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document doc = null;
		try {
			db = dbf.newDocumentBuilder();
			doc = db.parse(usersQueryService);
			// doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nodeList = null;
		try {
			nodeList = doc.getElementsByTagName("uri");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int s = 0; s < nodeList.getLength(); s++) {
			Node fstNode = nodeList.item(s);
			Element firstUserElement = (Element) fstNode;

			String userURI = firstUserElement.getTextContent();
			if (!userURI.contains("/ontologies/specific/AnonymousUser")) {

				User user = this._extractUser(firstUserElement, db);
				model.getUsers().add(user);
			}
		}

	}

	private void _extractWorkflows() {

		String workflowsQueryService = "http://rdf.myexperiment.org/sparql?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0Aselect+distinct+%3Fx+where+%7B%3Fx+rdf%3Atype+%3Chttp%3A%2F%2Frdf.myexperiment.org%2Fontologies%2Fcontributions%2FWorkflow%3E%7D+&formatting=XML&softlimit=5";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(workflowsQueryService);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nodeList = null;
		try {
			nodeList = doc.getElementsByTagName("uri");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int s = 0; s < nodeList.getLength(); s++) {
			Node fstNode = nodeList.item(s);
			Element firstUserElement = (Element) fstNode;
			Workflow workflow = this._extractWorkflow(firstUserElement);
			model.getWorflows().add(workflow);
		}

	}

	private void _extractRatings() {

		String workflowsQueryService = "http://rdf.myexperiment.org/sparql?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0Aselect+%3Fx+where+%7B%3Fx+rdf%3Atype+%3Chttp%3A%2F%2Frdf.myexperiment.org%2Fontologies%2Fannotations%2FRating%3E%7D%0D%0A&formatting=XML&softlimit=5";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(workflowsQueryService);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nodeList = null;
		try {
			nodeList = doc.getElementsByTagName("uri");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int s = 0; s < nodeList.getLength(); s++) {
			Node fstNode = nodeList.item(s);
			Element firstUserElement = (Element) fstNode;

			Rating rating = this._extractRating(firstUserElement);
			model.getRatings().add(rating);
		}

	}

	private void _extractFiles() {

		String workflowsQueryService = "http://rdf.myexperiment.org/sparql?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0Aselect+distinct+%3Fx+where+%7B%3Fx+rdf%3Atype+%3Chttp%3A%2F%2Frdf.myexperiment.org%2Fontologies%2Fcontributions%2FFile%3E%7D+&formatting=XML&softlimit=5";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(workflowsQueryService);
			doc.getDocumentElement().normalize();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nodeList = null;
		try {
			nodeList = doc.getElementsByTagName("uri");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int s = 0; s < nodeList.getLength(); s++) {
			Node fstNode = nodeList.item(s);
			Element firstUserElement = (Element) fstNode;
			File file = this._extractFile(firstUserElement);
			model.getFiles().add(file);
		}

	}

	private Rating _extractRating(Element ratingElement) {
		/*
		 * <ratings> <ID>86</ID>
		 * <ownerResource>http://www.myexperiment.org/users/459</ownerResource>
		 * <ownerURI>http://www.myexperiment.org/user.xml?id=459</ownerURI>
		 * <ratedElement
		 * >http://www.myexperiment.org/workflow.xml?id=117</ratedElement>
		 * <ratedElementID>117</ratedElementID> <ratingValue>5</ratingValue>
		 * <resource
		 * >http://www.myexperiment.org/workflows/117/ratings/86</resource>
		 * </ratings>
		 */
		Rating rating = new Rating();

		String ratingResource = ratingElement.getTextContent();
		rating.setResource(ratingResource);

		int indexOfRatings = ratingResource.indexOf("/ratings/");
		String ratingID = ratingResource.substring(indexOfRatings + 9,
				ratingResource.length());
		rating.setID(new Long(ratingID));

		String ratingURI = "http://www.myexperiment.org/rating.xml?id="
				+ ratingID + "&all_elements=yes";

		rating.setURI(ratingURI);

		try {
			Document doc = null;
			DocumentBuilderFactory dbf = null;
			dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(rating.getURI() + "&all_elements=yes");
			doc.getDocumentElement().normalize();

			// User name extraction
			NodeList nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("rating");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nodeList.getLength() > 0) {
				Element ratingElementRoot = (Element) nodeList.item(0);

				NodeList ratingNodeList = ratingElementRoot
						.getElementsByTagName("rating");

				Integer value = new Integer(
						((Element) ratingNodeList.item(0)).getTextContent());
				rating.setRatingValue(value);
			}

			// User name extraction
			nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("owner");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nodeList.getLength() > 0) {
				Node nameNode = nodeList.item(0);

				rating.setOwnerURI(((Element) nameNode).getAttribute("uri"));
				rating.setOwnerResource(((Element) nameNode)
						.getAttribute("resource"));
			}

			// User friends extractions

			nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("subject");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (nodeList.getLength() > 0) {
				Node nameNode = nodeList.item(0);

				NodeList workflowsNodeList = ((Element) nameNode)
						.getElementsByTagName("workflow");

				if (workflowsNodeList.getLength() > 0) {
					Node fstNode = workflowsNodeList.item(0);
					String ratedElementURI = ((Element) fstNode)
							.getAttribute("uri");

					rating.setRatedElement(ratedElementURI);

					int indexOfEqual = ratedElementURI.indexOf("=");
					String ratedElementID = ratedElementURI.substring(
							indexOfEqual + 1, ratedElementURI.length());
					rating.setRatedElementID(new Long(ratedElementID));
					System.out.println(">>>>>>.. " + rating.getRatedElement()
							+ " <<>> " + rating.getRatedElementID());
					rating.setType("WORKFLOW");

				}

				NodeList filesNodeList = ((Element) nameNode)
						.getElementsByTagName("file");

				if (filesNodeList.getLength() > 0) {
					Node fstNode = filesNodeList.item(0);
					String ratedElementURI = ((Element) fstNode)
							.getAttribute("uri");

					rating.setRatedElement(ratedElementURI);

					int indexOfEqual = ratedElementURI.indexOf("=");
					String ratedElementID = ratedElementURI.substring(
							indexOfEqual + 1, ratedElementURI.length());
					rating.setRatedElementID(new Long(ratedElementID));
					System.out.println(">>>>>>.. " + rating.getRatedElement()
							+ " <<>> " + rating.getRatedElementID());
					rating.setType("FILE");

				}

			}

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return rating;
	}

	private User _extractUser(Element userElement,
			DocumentBuilder documentBuilder) {

		/*
		 * HECHAS Cosas que extraer usuario Long ID; String URI; String
		 * resource; String description; String name = "whatever";
		 * ArrayList<String> friends;ArrayList<String> favouritedWorkflows;
		 * ArrayList<String> files; ArrayList<String> workflows;
		 * ArrayList<String> tagApplied;
		 */

		/*
		 * * NO HECHAS
		 * 
		 * ArrayList<String> groups;
		 */

		User user = new User();

		String userResource = userElement.getTextContent();
		user.setResource(userResource);

		int indexOfUsers = userResource.indexOf("/users/");
		String userID = userResource.substring(indexOfUsers + 7,
				userResource.length());
		user.setID(new Long(userID));

		String userURIBase = userResource.substring(0, indexOfUsers + 1);
		String userURI = userURIBase + "user.xml?id=" + userID;
		user.setURI(userURI);
		System.out.println("Extracting user" + user.getURI());
		
			try {
				Document doc = null;
			
				doc = documentBuilder
						.parse(user.getURI() + "&all_elements=yes");
				doc.getDocumentElement().normalize();

				// User name extraction
				NodeList nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("name");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					user.setName(((Element) nameNode).getTextContent());
				}

				// User description extraction
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("description");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					user.setDescription(((Element) nameNode).getTextContent());
				}

				// User friends extractions
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("friend");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				for (int s = 0; s < nodeList.getLength(); s++) {
					Node fstNode = nodeList.item(s);
					Element firstUserElement = (Element) fstNode;
					user.getFriends().add(firstUserElement.getAttribute("uri"));
				}

				// User workflows
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("workflows");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					NodeList workflowsNodeList = ((Element) nameNode)
							.getElementsByTagName("workflow");
					for (int s = 0; s < workflowsNodeList.getLength(); s++) {
						Node fstNode = workflowsNodeList.item(s);
						Element firstWorkflowElement = (Element) fstNode;
						user.getWorkflows().add(
								firstWorkflowElement.getAttribute("uri"));
					}
				}

				// User files
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("files");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					NodeList workflowsNodeList = ((Element) nameNode)
							.getElementsByTagName("file");
					for (int s = 0; s < workflowsNodeList.getLength(); s++) {
						Node fstNode = workflowsNodeList.item(s);
						Element firstWorkflowElement = (Element) fstNode;
						user.getFiles().add(
								firstWorkflowElement.getAttribute("uri"));
					}
				}
				
				// User packs
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("packs");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					NodeList packsNodeList = ((Element) nameNode)
							.getElementsByTagName("pack");
					for (int s = 0; s < packsNodeList.getLength(); s++) {
						Node fstNode = packsNodeList.item(s);
						Element firstPackElement = (Element) fstNode;
						user.getPacks().add(
								firstPackElement.getAttribute("uri"));
					}
				}
				

				// User favourites
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("favourited");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					NodeList workflowsNodeList = ((Element) nameNode)
							.getElementsByTagName("workflow");
					for (int s = 0; s < workflowsNodeList.getLength(); s++) {
						Node fstNode = workflowsNodeList.item(s);
						Element firstWorkflowElement = (Element) fstNode;
						user.getFavouritedWorkflows().add(
								firstWorkflowElement.getAttribute("uri"));
					}

					NodeList filesNodeList = ((Element) nameNode)
							.getElementsByTagName("file");
					for (int s = 0; s < filesNodeList.getLength(); s++) {
						Node fstNode = filesNodeList.item(s);
						Element firsFileElement = (Element) fstNode;
						user.getFavouritedFiles().add(
								firsFileElement.getAttribute("uri"));
					}

				}

		
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("taggings-applied");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node taggingsNode = nodeList.item(0);

					NodeList taggingsNodeList = ((Element) taggingsNode)
							.getElementsByTagName("tagging");
					for (int s = 0; s < taggingsNodeList.getLength(); s++) {
						Node fstNode = taggingsNodeList.item(s);
						Element firstTaggingElement = (Element) fstNode;

						String tag = firstTaggingElement.getTextContent();
						user.addTagging(tag);

					}
				}

				// User workflows
				/*
				 * } catch (ParserConfigurationException e) { // TODO
				 * Auto-generated catch block e.printStackTrace();
				 */
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
		return user;
	}

	private String _convertWorkflowResourceToURI(String workflowResource){
		int indexOfWorkflows = workflowResource.indexOf("/workflows/");
		String workflowID = workflowResource.substring(indexOfWorkflows + 11,
				workflowResource.length());
		

		String workflowURI = "http://www.myexperiment.org/workflow.xml?id="
				+ workflowID;
		return workflowURI;
	}
	
	private Workflow _extractWorkflow(Element workflowElement) {
		/*
		 * Long id; String URI; String resource; String description; String
		 * title; String contentType; String contentURI; String uploaderURI;
		 * ArrayList<String> tags;
		 */

		Workflow workflow = new Workflow();
		String workflowResource = workflowElement.getTextContent();
		workflow.setResource(workflowResource);

		int indexOfWorkflows = workflowResource.indexOf("/workflows/");
		String workflowID = workflowResource.substring(indexOfWorkflows + 11,
				workflowResource.length());
		workflow.setId(new Long(workflowID));

		String workflowURI = "http://www.myexperiment.org/workflow.xml?id="
				+ workflowID;

		workflow.setURI(workflowURI);

		System.out.println("Extracting workflow" + workflow.getURI());

		try {
			Document doc = null;
			DocumentBuilderFactory dbf = null;
			dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			doc = db.parse(workflow.getURI() + "&all_elements=yes");
			// doc.getDocumentElement().normalize();

			// Workflow title extraction
			NodeList nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("title");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			workflow.setTitle(((Element) nodeList.item(0)).getTextContent());

		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return workflow;
	}

	private String _convertFileResourceToURI(String fileResource){
		int indexOfFiles = fileResource.indexOf("/files/");
		String fileID = fileResource.substring(indexOfFiles + 7,
				fileResource.length());
		

		String fileURIBase = fileResource.substring(0,
				indexOfFiles + 1);
		String fileURI = fileURIBase + "file.xml?id=" + fileID;
		return fileURI;
		
	}
	
	private File _extractFile(Element workflowElement) {
		/*
		 * Long id; String URI; String resource; String description; String
		 * title; String contentType; String contentURI; String uploaderURI;
		 * ArrayList<String> tags;
		 */

		File workflow = new File();
		String workflowResource = workflowElement.getTextContent();
		workflow.setResource(workflowResource);

		int indexOfWorkflows = workflowResource.indexOf("/files/");
		String workflowID = workflowResource.substring(indexOfWorkflows + 7,
				workflowResource.length());
		workflow.setId(new Long(workflowID));

		String fileURIBase = workflowResource.substring(0,
				indexOfWorkflows + 1);
		String workflowURI = fileURIBase + "file.xml?id=" + workflowID;

		workflow.setUri(workflowURI);

		return workflow;
	}

	public void marshallToFile(String fileName) {

		try {
			JAXBContext context = JAXBContext.newInstance(Model.class);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// m.marshal(this.model, System.out);

			Writer w = null;
			try {
				w = new FileWriter(fileName);
				m.marshal(this.model, w);
			} finally {
				try {
					w.close();
				} catch (Exception e) {
				}
			}
		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
		private void _extractPacks() {
			String packsQueryService = "http://rdf.myexperiment.org/sparql?query=PREFIX+rdf%3A+%3Chttp%3A%2F%2Fwww.w3.org%2F1999%2F02%2F22-rdf-syntax-ns%23%3E%0D%0Aselect+distinct+%3Fx+where+%7B%3Fx+rdf%3Atype+%3Chttp%3A%2F%2Frdf.myexperiment.org%2Fontologies%2Fpacks%2FPack%3E%7D%0D%0A&formatting=XML&softlimit=5";
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc = null;
			try {
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(packsQueryService);
				doc.getDocumentElement().normalize();
			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			NodeList nodeList = null;
			try {
				nodeList = doc.getElementsByTagName("uri");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int s = 0; s < nodeList.getLength(); s++) {
				Node fstNode = nodeList.item(s);
				Element firstUserElement = (Element) fstNode;
				Pack file = this._extractPack(firstUserElement);
				model.getPacks().add(file);
			}
		}
	
		private Pack _extractPack(Element packElement) {
			/*
			 * Long id; String URI; String resource; String description; String
			 * title; String contentType; String contentURI; String uploaderURI;
			 * ArrayList<String> tags;
			 */

			Pack pack = new Pack();
			String packResource = packElement.getTextContent();
			pack.setResource(packResource);

			int indexOfPacks = packResource.indexOf("/packs/");
			String packID = packResource.substring(indexOfPacks + 7,
					packResource.length());
			pack.setId(new Long(packID));

			String packURI = "http://www.myexperiment.org/pack.xml?id="
					+ packID;

			pack.setURI(packURI);

			System.out.println("Extracting pack" + pack.getURI());

			try {
				Document doc = null;
				DocumentBuilderFactory dbf = null;
				dbf = DocumentBuilderFactory.newInstance();
				DocumentBuilder db = dbf.newDocumentBuilder();
				doc = db.parse(pack.getURI() + "&all_elements=yes");
				// doc.getDocumentElement().normalize();

				// Workflow title extraction
				NodeList nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("title");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				pack.setTitle(((Element) nodeList.item(0)).getTextContent());
				
				
				// User favourites
				nodeList = null;
				try {
					nodeList = doc.getElementsByTagName("internal-pack-items");
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if (nodeList.getLength() > 0) {
					Node nameNode = nodeList.item(0);

					NodeList workflowsNodeList = ((Element) nameNode)
							.getElementsByTagName("workflow");
					for (int s = 0; s < workflowsNodeList.getLength(); s++) {
						Node fstNode = workflowsNodeList.item(s);
						Element firstWorkflowElement = (Element) fstNode;
						
						String workflowResource = firstWorkflowElement.getAttribute("resource");
						pack.getInternalWorkflows().add(this._convertWorkflowResourceToURI(workflowResource));
								
					}

					NodeList filesNodeList = ((Element) nameNode)
							.getElementsByTagName("file");
					for (int s = 0; s < filesNodeList.getLength(); s++) {
						Node fstNode = filesNodeList.item(s);
						Element firsFileElement = (Element) fstNode;
						String fileResource = firsFileElement.getAttribute("resource");
						pack.getInternalFiles().add(this._convertFileResourceToURI(fileResource));
					}

				}

				

			} catch (ParserConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return pack;
		}

	/*
	 * Cosas que extraer de workflow
	 * 
	 * Long id; String URI; String resource; String description; String title;
	 * String contentType; String contentURI; String uploaderURI;
	 * ArrayList<String> tags;
	 */

}
