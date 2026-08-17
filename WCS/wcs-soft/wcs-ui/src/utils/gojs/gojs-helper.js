import go from "@/lib/js/go.js";

const $ = go.GraphObject.make;

export const STATUS_COLORS = {
  default: {
    stroke: "#9CA3AF",
    strokeWidth: 2,
    name: "默认",
  },
  running: {
    stroke: "#10B981",
    strokeWidth: 3,
    name: "运行",
  },
  idle: {
    stroke: "#3B82F6",
    strokeWidth: 2,
    name: "空闲",
  },
  abnormal: {
    stroke: "#EF4444",
    strokeWidth: 4,
    name: "异常",
  },
  warning: {
    stroke: "#F59E0B",
    strokeWidth: 3,
    name: "警告",
  },
  offline: {
    stroke: "#6B7280",
    strokeWidth: 2,
    name: "离线",
  },
  maintenance: {
    stroke: "#6B7280",
    strokeWidth: 2,
    name: "禁用",
  },
  hasStockWithTask: {
    stroke: "#10B981",
    strokeWidth: 3,
    name: "出站",
  },
  hasStockWithoutTask: {
    stroke: "#3B82F6",
    strokeWidth: 2,
    name: "有货",
  },
  noStockWithTask: {
    stroke: "#F59E0B",
    strokeWidth: 3,
    name: "入站",
  },
  noStockWithoutTask: {
    stroke: "#9CA3AF",
    strokeWidth: 2,
    name: "无货",
  },
};

export function getStatusColor(status) {
  return STATUS_COLORS[status] || STATUS_COLORS.default;
}

export function createLinkTemplate() {
  return $(
    go.Link,
    {
      routing: go.Link.AvoidsNodes,
      curve: go.Link.JumpOver,
      corner: 5,
      toShortLength: 4,
      relinkableFrom: true,
      relinkableTo: true,
      reshapable: true,
      resegmentable: true,
      mouseEnter: function (e, link) {
        link.findObject("HIGHLIGHT").stroke = "rgba(30,144,255,0.2)";
      },
      mouseLeave: function (e, link) {
        link.findObject("HIGHLIGHT").stroke = "transparent";
      },
      selectionAdorned: false,
    },
    new go.Binding("points").makeTwoWay(),
    $(
      go.Shape,
      {
        isPanelMain: true,
        strokeWidth: 8,
        stroke: "transparent",
        name: "HIGHLIGHT",
      }
    ),
    $(
      go.Shape,
      { isPanelMain: true, stroke: "#6b7280", strokeWidth: 3 },
      new go.Binding("stroke", "isSelected", function (sel) {
        return sel ? "#3b82f6" : "#6b7280";
      }).ofObject()
    ),
    $(
      go.Shape,
      { toArrow: "standard", strokeWidth: 0, fill: "#6b7280" }
    ),
    $(
      go.Panel,
      "Auto",
      {
        visible: false,
        name: "LABEL",
        segmentIndex: 2,
        segmentFraction: 0.5,
      },
      new go.Binding("visible", "visible").makeTwoWay(),
      $(
        go.Shape,
        "RoundedRectangle",
        { fill: "#F8F8F8", strokeWidth: 0 }
      ),
      $(
        go.TextBlock,
        "Yes",
        {
          textAlign: "center",
          font: "10pt helvetica, arial, sans-serif",
          stroke: "#333333",
          editable: true,
        },
        new go.Binding("text").makeTwoWay()
      )
    )
  );
}

export function createNodeTemplate(gridSize, clickHandler, mouseEnterHandler, mouseLeaveHandler) {
  const defaultSize = new go.Size(100, 100);
  
  return $(
    go.Node,
    "Spot",
    {
      resizable: true,
      locationSpot: new go.Spot(0, 0, gridSize.width, gridSize.height),
    },
    new go.Binding("position", "pos", go.Point.parse).makeTwoWay(go.Point.stringify),
    $(
      go.Panel,
      "Auto",
      {
        desiredSize: defaultSize,
      },
      new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(go.Size.stringify),
      $(
        go.Shape,
        "Rectangle",
        {
          fill: "white",
          stroke: "#000000ff",
          strokeWidth: 5,
        },
        new go.Binding("stroke", "status", function (status) {
          return getStatusColor(status).stroke;
        }),
        new go.Binding("strokeWidth", "status", function (status) {
          return getStatusColor(status).strokeWidth;
        })
      ),
      $(
        go.Picture,
        new go.Binding("source"),
        new go.Binding("desiredSize", "size", function(sizeStr) {
          var size = go.Size.parse(sizeStr);
          if (size) {
            return new go.Size(Math.max(0, size.width - 5), Math.max(0, size.height - 5));
          }
          return null;
        }),
      )
    ),
    $(
      go.Panel,
      "Auto",
      {
        alignment: go.Spot.RightCenter,
        alignmentFocus: go.Spot.LeftCenter,
        margin: new go.Margin(0, 0, 0, 8),
      },
      $(
        go.Shape,
        "RoundedRectangle",
        {
          fill: "rgba(255, 255, 255, 0.95)",
          stroke: "#E4E7ED",
          strokeWidth: 1,
          parameter1: 4,
          parameter2: 4,
        }
      ),
      $(
        go.Panel,
        "Vertical",
        {
          margin: new go.Margin(4, 8, 4, 8),
        },
        $(
          go.TextBlock,
          {
            font: "bold 13px Microsoft YaHei, sans-serif",
            stroke: "#303133",
            textAlign: "left",
            maxSize: new go.Size(140, NaN),
          },
          new go.Binding("text", "positionCode")
        ),
        $(
          go.TextBlock,
          {
            font: "12px Microsoft YaHei, sans-serif",
            stroke: "#606266",
            textAlign: "left",
            maxSize: new go.Size(140, NaN),
          },
          new go.Binding("text", "statusDescription")
        )
      )
    ),
    {
      click: clickHandler,
      cursor: "pointer",
    },
    {
      mouseEnter: mouseEnterHandler,
    },
    {
      mouseLeave: mouseLeaveHandler,
    }
  );
}

export function createNodeTemplateForUpdate(gridSize, clickHandler) {
  const defaultSize = new go.Size(100, 100);
  
  return $(
    go.Node,
    "Auto",
    {
      resizable: true,
      locationSpot: new go.Spot(0, 0, gridSize.width, gridSize.height),
      desiredSize: defaultSize,
    },
    new go.Binding("position", "pos", go.Point.parse).makeTwoWay(go.Point.stringify),
    new go.Binding("desiredSize", "size", go.Size.parse).makeTwoWay(go.Size.stringify),
    $(
      go.Shape,
      "Rectangle",
      {
        fill: "white",
        stroke: "#000000ff",
        strokeWidth: 5,
      },
      new go.Binding("stroke", "status", function (status) {
        return getStatusColor(status).stroke;
      }),
      new go.Binding("strokeWidth", "status", function (status) {
        return getStatusColor(status).strokeWidth;
      })
    ),
    $(
      go.Picture,
      new go.Binding("source"),
      new go.Binding("desiredSize", "size", function(sizeStr) {
        var size = go.Size.parse(sizeStr);
        if (size) {
          return new go.Size(Math.max(0, size.width - 5), Math.max(0, size.height - 5));
        }
        return null;
      }),
    ),
    {
      click: clickHandler,
      cursor: "pointer",
    }
  );
}

export function createCellNodeTemplate() {
  var CellSize = new go.Size(50, 50);
  
  return $(
    go.Node,
    nodeStyle(),
    $(
      go.Panel,
      "Spot",
      $(
        go.Shape,
        "Rectangle",
        {
          desiredSize: CellSize,
          strokeWidth: 5,
        },
        new go.Binding("fill", "fillColor"),
        new go.Binding("stroke", "borderColor")
      ),
      $(go.TextBlock, "", textStyle(), new go.Binding("text", "text"))
    )
  );
}

export function createBackgroundPart(backgroundImg) {
  var pictureProps = (backgroundImg != null && backgroundImg !== "")
    ? (typeof backgroundImg === 'string' ? { source: backgroundImg } : backgroundImg)
    : null;
  
  var backgroundPart = $(go.Part, {
    layerName: "Background",
    position: new go.Point(0, 0),
    selectable: false,
    pickable: false,
  });
  
  if (pictureProps) {
    backgroundPart.add($(go.Picture, pictureProps));
  }
  
  return backgroundPart;
}

export function createPaletteNodeTemplate() {
  return $(
    go.Node,
    "Horizontal",
    $(
      go.Panel,
      "Auto",
      $(
        go.TextBlock,
        { margin: 0, width: 20, height: 20, background: "white" },
        new go.Binding("text", "id")
      ),
      $(
        go.Picture,
        { margin: 0, width: 20, height: 20, background: "white" },
        new go.Binding("source")
      )
    )
  );
}

export function nodeStyle() {
  return [
    new go.Binding("location", "loc", go.Point.parse).makeTwoWay(go.Point.stringify),
    {
      locationSpot: go.Spot.TopLeft,
    },
  ];
}

export function textStyle() {
  return {
    font: "bold 8pt Lato, Helvetica, Arial, sans-serif",
    stroke: "#303133",
  };
}

export function showLinkLabel(e) {
  var label = e.subject.findObject("LABEL");
  if (label !== null)
    label.visible = e.subject.fromNode.data.category === "Conditional";
}

export function initDiagram(viewConfig) {
  const {
    containerId,
    gridSize = { width: 50, height: 50 },
    isReadOnly = false,
    showGrid = false,
    clickHandler,
    mouseEnterHandler,
    mouseLeaveHandler,
    changedSelectionHandler,
    backgroundImg = null,
  } = viewConfig;

  var CellSize = new go.Size(gridSize.width, gridSize.height);

  const diagram = $(
    go.Diagram,
    containerId,
    {
      grid: showGrid ? $(
        go.Panel,
        "Grid",
        { gridCellSize: CellSize },
        $(go.Shape, "LineH", { stroke: "#e5e7eb" }),
        $(go.Shape, "LineV", { stroke: "#e5e7eb" })
      ) : null,
      "draggingTool.isGridSnapEnabled": showGrid,
      "resizingTool.isGridSnapEnabled": true,
      LinkDrawn: showLinkLabel,
      LinkRelinked: showLinkLabel,
      "undoManager.isEnabled": true,
      isReadOnly: isReadOnly,
      scale: 1,
      minScale: 0.1,
      maxScale: 4,
      "animationManager.isEnabled": false,
      "layout.isOngoing": false,
      "toolManager.mouseWheelBehavior": go.ToolManager.WheelZoom,
    }
  );

  if (backgroundImg) {
    diagram.add(createBackgroundPart(backgroundImg));
  }

  const linkTemplate = createLinkTemplate();
  diagram.linkTemplate = linkTemplate;

  if (isReadOnly) {
    const nodeTemplate = createNodeTemplate(gridSize, clickHandler, mouseEnterHandler, mouseLeaveHandler);
    diagram.nodeTemplate = nodeTemplate;
    diagram.nodeTemplateMap.add("cell", createCellNodeTemplate());
  } else {
    const nodeTemplate = createNodeTemplateForUpdate(gridSize, clickHandler);
    diagram.nodeTemplate = nodeTemplate;
  }

  diagram.toolManager.linkingTool.temporaryLink.routing = go.Link.Orthogonal;
  diagram.toolManager.relinkingTool.temporaryLink.routing = go.Link.Orthogonal;

  if (showGrid) {
    diagram.toolManager.draggingTool.gridSnapCellSpot = go.Spot.TopLeft;
  }

  if (isReadOnly) {
    diagram.maxSelectionCount = 1;
  }

  if (changedSelectionHandler) {
    diagram.changedSelection = changedSelectionHandler;
  }

  return diagram;
}

export function initPalette(containerId, clickHandler) {
  const palette = $(
    go.Palette,
    containerId,
    {
      "animationManager.initialAnimationStyle": go.AnimationManager.None,
      model: new go.GraphLinksModel([]),
    }
  );

  palette.nodeTemplate = createPaletteNodeTemplate();
  
  if (clickHandler) {
    palette.nodeTemplate.click = clickHandler;
  }

  return palette;
}

export function loadDiagramData(diagram, data) {
  if (!diagram || !data) return;
  diagram.model = go.Model.fromJson(data);
}

export function addNodesToPalette(palette, nodes) {
  if (!palette || !nodes || !Array.isArray(nodes)) return;
  
  if (palette.model.nodeDataArray.length > 0) {
    palette.model.nodeDataArray = [];
  }
  
  nodes.forEach((element) => {
    palette.model.addNodeData({
      size: "50 50",
      type: element.name,
      source: element.imgUrl,
      id: element.id,
    });
  });
}

export function updateNodeImage(diagram, deviceCode, newSource) {
  if (!diagram) return;
  
  var nodes = diagram.model.nodeDataArray;
  var model = diagram.model;
  
  nodes.forEach((node) => {
    if (node.deviceCode == deviceCode) {
      model.setDataProperty(node, "source", newSource);
    }
  });
}

export function zoomToFit(diagram) {
  if (!diagram) return;
  diagram.zoomToFit();
}

export function setInitialAutoScale(diagram, scaleType) {
  if (!diagram) return;
  diagram.startTransaction("set initialAutoScale");
  diagram.initialAutoScale = scaleType;
  diagram.commitTransaction("set initialAutoScale");
}