# PaginationScrollView(Lite-Launcher3)
This repository bases on Launcher3,only include some viewgroups,such as LauncherRootView(PaginationScrollView),Workspace,CellLayout and so on.The destination is to build a lite launcher3 to complete some function below:

- 1. Items display in grid layout and each grid layout in a page,
- 2. Each page can be scrolled left and right by finger,
- 3. Single item can be dragged to another page,
- 4. Other item will be re-range when finger drag one item,

# WHY
RecylerView may fullfill these function,but you must customize LayoutManager and related classes.In our company's project,we want to display all installed applications icon in a list,so we develop launcher3 to accomplish all requirements.As you know,Launcher3 is a complex and comlicated project,importing such a big monster for some requirements is worthless.

# HOW

Deleting database operation temporarily,and it will be included in a library module in my plan.

Deleting folder and widget icons in workspace temporarily,and it will be added back when application-icon functions are stable.

Deleting Launcher class and turn it to PaginationScrollView.

# Implementation

None.Fork this repository and modify codes to adjust your requirements,the detail usage is in app/MainActivity.
