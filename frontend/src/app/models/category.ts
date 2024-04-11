export interface Category {
  id?: number;
  name: string;
  icon: string;
  order: number;
  type: CategoryType;
  subTypes?: Category[];
}

export enum CategoryType {
  EXPENSE,
  INCOME,
}
