export function unreadNotificationCount(items = []) {
  return items.filter((item) => !item.readFlag).length;
}

export function canOpenNotificationCase(item) {
  return Boolean(item?.caseId);
}
