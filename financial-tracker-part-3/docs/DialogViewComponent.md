# Ivy DialogView Komponente (Axon Ivy 12)

## Zweck
Die Komponente kapselt das Öffnen von PrimeFaces Dynamic Dialogs über die Public Axon Ivy API `IvyDynamicDialog`. Dadurch kann eine View als Parameter übergeben werden, inkl. optionaler Dialog-Optionen und Parameter.

## Funktionsweise
- Die Komponente rendert einen `p:commandButton`.
- Beim Klick wird `DialogViewBean.open(...)` aufgerufen.
- Der Bean normalisiert den View-Namen (entfernt eine optionale `.xhtml` Endung).
- Wenn keine Optionen/Parameter angegeben sind, wird `IvyDynamicDialog.open(viewName)` genutzt.
- Andernfalls wird ein `DialogFrameworkOptions` Builder erstellt, optionale Flags gesetzt und `IvyDynamicDialog.open(viewName, options, params)` aufgerufen.

## Komponenten-Interface
Datei: [financial-tracker-part-3/src_hd/financial/tracker/part3/DialogView/DialogView.xhtml](financial-tracker-part-3/src_hd/financial/tracker/part3/DialogView/DialogView.xhtml)

Wichtige Attribute:
- `view` (required): View-Name ohne `.xhtml`. Relativ: muss im gleichen Package wie die aufrufende HTML-Dialog-View liegen. Absolut: mit `/` beginnen; `.xhtml` wird bei absoluten Pfaden automatisch ergaenzt.
- `value`: Button-Label (Default: "Open Dialog").
- `icon`: Button-Icon (Default: `pi pi-external-link`).
- `styleClass`: CSS-Klassen für den Button.
- `action`: `open` oder `close` (Default: `open`).
- `width`, `height`: Dialoggröße (als Content-Größe, Default: 600/300). Bei leerem Dialog bitte explizit setzen.
- `modal`, `resizable`, `draggable`: Basisoptionen.
- `options`: Map für zusätzliche Dialog-Optionen (siehe unten).
- `params`: Map für Dialog-Parameter.
- `returnListener`: Listener für `dialogReturn`.
- `data`: Beliebiges Objekt, das im Dialog bearbeitet werden soll.
- `dataKeyParam`: Name des Request-Parameters, der den Schlüssel für `dialogDataStore` enthält (Default: `dialogDataKey`).

## Unterstützte Optionen in `options`
Aktuell werden folgende Schlüssel unterstützt:
- `width`, `height`, `contentWidth`, `contentHeight` (String/Number)
- `modal`, `resizable`, `draggable`, `closable`, `closeOnEscape`, `fitViewport`, `responsive`, `blockScroll` (Boolean)
- `styleClass` (String)

Unbekannte Optionen werden protokolliert.

## Verwendung (Beispiel)
```xhtml
<ic:financial.tracker.part3.DialogView
  view="SomeDialog"
  value="Dialog öffnen"
  width="800"
  height="600"
  modal="true"
  resizable="false"
  draggable="true"
/>
```

## Absolute View-Pfade
Absolute Pfade funktionieren nur fuer klassische JSF-Views (z. B. unter `webContent`).
Fuer HTML-Dialogs aus `src_hd` liefert PrimeFaces keine NavigationCase; hier brauchst du eine Wrapper-View im gleichen Package wie der aufrufende Dialog.

Beispiel Wrapper im aufrufenden Package:
```xhtml
<ui:composition template="/layouts/frame-10-full-width.xhtml">
  <ui:define name="content">
    <ui:include src="../CalledPopupView/CalledPopupViewContent.xhtml" />
  </ui:define>
</ui:composition>
```

## Schließen des Dialogs
```xhtml
<ic:financial.tracker.part3.DialogView
  view="SomeDialog"
  action="close"
  value="Dialog schließen"
  icon="pi pi-times"
/>
```

## Rückgabe (dialogReturn)
Wenn `returnListener` gesetzt ist, wird der `dialogReturn` Event auf dem Button registriert und an den Listener weitergereicht.

Beispiel: Dialog schliesst sich mit Rueckgabewert
```java
public void onPersonDialogReturn(SelectEvent event) {
  Object returned = event.getObject();
}
```

## Objekte an den Dialog übergeben (und zurückschreiben)
Die Komponente kann Objekte über eine Session-Map bereitstellen. Dabei wird **die gleiche Objekt-Referenz** verwendet. Änderungen im Dialog sind nach dem Schließen im aufrufenden View sichtbar, sobald dieser aktualisiert wird (z. B. `update="@form"` via `dialogReturn`).

### Öffnen mit Objekt
```xhtml
<ic:financial.tracker.part3.DialogView
  view="EditTransaction"
  value="Bearbeiten"
  data="#{data.transaction}"
  returnListener="#{logic.onDialogReturn}"
  update="@form"
/>
```

### Zugriff im Dialog
```xhtml
<ui:fragment rendered="#{not empty param.dialogDataKey}">
  <ui:param name="tx" value="#{dialogDataStore.get(param.dialogDataKey)}" />
  <!-- tx bearbeiten -->
</ui:fragment>
```

### Hinweise
- Das Objekt muss mutierbar sein, damit Änderungen übernommen werden.
- Die Dialog-View und der aufrufende View teilen nicht denselben ViewScope; daher erfolgt die Übergabe über `dialogDataStore` (SessionScope).

## Implementierung
- Bean: [financial-tracker-part-3/src/beans/DialogViewBean.java](financial-tracker-part-3/src/beans/DialogViewBean.java)
- Öffnung über `ch.ivyteam.ivy.jsf.primefaces.dialog.IvyDynamicDialog`
- Optionen über `org.primefaces.model.DialogFrameworkOptions`

## Hinweise
- Der View-Name darf kein `.xhtml` enthalten; die Komponente entfernt die Endung jedoch automatisch.
- View muss im gleichen Package liegen wie die HTML-Dialog-View (Axon Ivy Vorgabe).
