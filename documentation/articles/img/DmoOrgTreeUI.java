
package com.dmo.util.ui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.EventRequest;
import javax.portlet.EventResponse;
import javax.portlet.PortletMode;
import javax.portlet.PortletRequest;
import javax.portlet.PortletSession;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ResourceRequest;
import javax.portlet.ResourceResponse;
import javax.servlet.annotation.WebServlet;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.model.Organization;
import com.liferay.portal.model.User;
import com.liferay.portal.service.OrganizationLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.HierarchicalContainer;
import com.vaadin.event.ItemClickEvent;
import com.vaadin.server.VaadinPortletSession;
import com.vaadin.server.VaadinPortletSession.PortletListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Tree;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings({
	"serial", "deprecation"
})
@Theme("dmoprojectview")
public class DmoOrgTreeUI extends UI implements PortletListener {

	private PortletMode previousMode = null;
	private PortletRequest portletRequest;
	private PortletSession portletSession;
	private User user;
	private ThemeDisplay themeDisplay;
	private VerticalLayout viewContent = new VerticalLayout();
	private Tree tree = new Tree("Organization Tree");
	private HashSet<Long> checked = new HashSet<Long>();

	@WebServlet(value = "/*", asyncSupported = true)
	@VaadinServletConfiguration(productionMode = false, ui = DmoOrgTreeUI.class)
	public static class Servlet extends VaadinServlet {
	}

	@Override
	protected void init(VaadinRequest request) {

		viewContent = new VerticalLayout();
		viewContent.setMargin(true);
		setContent(viewContent);

		if (VaadinSession.getCurrent() instanceof VaadinPortletSession) {
			final VaadinPortletSession portletsession = (VaadinPortletSession) VaadinSession.getCurrent();
			portletsession.addPortletListener(this);

			try {
				setPortletRequestUI((PortletRequest) request);
				setPortletSessionUI(portletsession.getPortletSession());
				user = UserLocalServiceUtil.getUser(PortalUtil.getUser((PortletRequest) request).getUserId());
				setThemeDisplayUI((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));
				//System.out.println("DEBUG=>" + this.getClass() + "\n     ==>themeDisplay getLayout=" + themeDisplay.getLayout().toString());
				doView();
			}
			catch (PortalException e) {
				e.printStackTrace();
			}
			catch (com.liferay.portal.kernel.exception.SystemException e) {
				e.printStackTrace();
			}
		}
		else {
			Notification.show("Not initialized in a Portal!", Notification.Type.ERROR_MESSAGE);
		}

	}

	@Override
	public void handleRenderRequest(RenderRequest request, RenderResponse response, UI root) {

		PortletMode portletMode = request.getPortletMode();
		try {
			setPortletRequestUI((PortletRequest) request);
			setPortletSessionUI(request.getPortletSession());

			setThemeDisplayUI((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));
			user = UserLocalServiceUtil.getUser(PortalUtil.getUser((PortletRequest) request).getUserId());
			
			if (request.getPortletMode() == PortletMode.VIEW) {
				doView();
			}
		}
		catch (PortalException e) {
			Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
		}
		catch (com.liferay.portal.kernel.exception.SystemException e) {
			Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
		}

		setPreviousModeUI(portletMode);

	}

	@Override
	public void handleActionRequest(ActionRequest request, ActionResponse response, UI root) {

	}

	@Override
	public void handleEventRequest(EventRequest request, EventResponse response, UI root) {

	}

	@Override
	public void handleResourceRequest(ResourceRequest request, ResourceResponse response, UI root) {

		this.setThemeDisplayUI((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));

		setPortletRequestUI((PortletRequest) request);
		setPortletSessionUI(request.getPortletSession());
		setThemeDisplayUI((ThemeDisplay) request.getAttribute(WebKeys.THEME_DISPLAY));
		try {
			user = UserLocalServiceUtil.getUser(PortalUtil.getUser((PortletRequest) request).getUserId());
		}
		catch (PortalException e) {
			Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
		}
		catch (com.liferay.portal.kernel.exception.SystemException e) {
			Notification.show(e.getMessage(), Type.ERROR_MESSAGE);
		}
	}

	public void doView() {

		try {
			buildMainLayout();
		}
		catch (SystemException e) {
			Notification.show("System error occurred.  Contact administrator.", Type.WARNING_MESSAGE);
		}
		catch (PortalException e) {
			Notification.show("System error occurred.  Contact administrator.", Type.WARNING_MESSAGE);
		}
		catch (Exception e) {
			Notification.show("System error occurred.  Contact administrator.", Type.WARNING_MESSAGE);
		}
	}

	private void buildMainLayout()
		throws SystemException, PortalException {

		if (viewContent.getComponentCount() > 0) {
			viewContent.removeAllComponents();
		}

		viewContent.setMargin(true);
		viewContent.addStyleName("view");

		List<Organization> orgList = new ArrayList<Organization>();
		orgList = OrganizationLocalServiceUtil.getUserOrganizations(user.getUserId());
		final HierarchicalContainer container = createTreeContent(orgList);

		tree = new Tree("My Organizations", container);
		tree.addStyleName("checkboxed");
		tree.setSelectable(false);
		tree.setItemCaptionMode(ItemCaptionMode.PROPERTY);
		tree.setItemCaptionPropertyId("name");
		tree.addItemClickListener(new ItemClickEvent.ItemClickListener() {

			public void itemClick(ItemClickEvent event) {

				if (event.getItemId().getClass() == Long.class) {
					long itemId = (Long) event.getItemId();
					if (checked.contains(itemId)) {
						checkboxChildren(container, itemId, false);
					}
					else {
						checkboxChildren(container, itemId, true);
						tree.expandItemsRecursively(itemId);
					}
				}
				tree.markAsDirty();
			}
		});

		Tree.ItemStyleGenerator itemStyleGenerator = new Tree.ItemStyleGenerator() {

			@Override
			public String getStyle(Tree source, Object itemId) {

				if (checked.contains(itemId))
					return "checked";
				else
					return "unchecked";
			}
		};
		tree.setItemStyleGenerator(itemStyleGenerator);

		viewContent.addComponent(tree);
		viewContent.setVisible(true);
		setContent(viewContent);
	}

	public void checkboxChildren(HierarchicalContainer hc, long itemId, boolean bAdd) {

		try {

			if (bAdd) {
				checked.add(itemId);
			}
			else {
				checked.remove(itemId);
			}

			if (hc.hasChildren(itemId)) {
				Collection<?> children = hc.getChildren(itemId);
				for (Object o : children) {
					if (o.getClass() == Long.class) {
						itemId = (Long) o;
						checkboxChildren(hc, itemId, bAdd);
					}
				}
			}
		}
		catch (Exception e) {
			Notification.show("Unable to build Organization tree.  Contact Administrator.", Type.ERROR_MESSAGE);
		}
	}

	public static HierarchicalContainer createTreeContent(List<Organization> oTrees)
		throws SystemException, PortalException {

		HierarchicalContainer container = new HierarchicalContainer();
		container.addContainerProperty("name", String.class, "");

		new Object() {

			@SuppressWarnings("unchecked")
			public void put(List<Organization> data, HierarchicalContainer container)
				throws SystemException, PortalException {

				for (Organization o : data) {
					long orgId = o.getOrganizationId();

					if (!container.containsId(orgId)) {

						container.addItem(orgId);
						container.getItem(orgId).getItemProperty("name").setValue(o.getName());

						if (!o.hasSuborganizations()) {
							container.setChildrenAllowed(orgId, false);
						}
						else {
							container.setChildrenAllowed(orgId, true);
						}

						if (o.isRoot()) {
							container.setParent(orgId, null);
						}
						else {
							if (!container.containsId(o.getParentOrganizationId())) {
								List<Organization> sub = new ArrayList<Organization>();
								sub.add(o.getParentOrganization());
								put(sub, container);
							}

							container.setParent(orgId, (Object) o.getParentOrganizationId());
						}
					}
				}
			}
		}.put(oTrees, container);

		return container;
	}

	public PortletRequest getPortletRequestUI() {

		return portletRequest;
	}

	public void setPortletRequestUI(PortletRequest portletRequest) {

		this.portletRequest = portletRequest;
	}

	public PortletSession getPortletSessionUI() {

		return portletSession;
	}

	public void setPortletSessionUI(PortletSession portletSession) {

		this.portletSession = portletSession;
	}

	public ThemeDisplay getThemeDisplayUI() {

		return themeDisplay;
	}

	public void setThemeDisplayUI(ThemeDisplay themeDisplay) {

		this.themeDisplay = themeDisplay;
	}

	public PortletMode getPreviousModeUI() {

		return previousMode;
	}

	public void setPreviousModeUI(PortletMode previousMode) {

		this.previousMode = previousMode;
	}

}
