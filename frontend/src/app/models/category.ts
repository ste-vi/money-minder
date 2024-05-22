export interface Category {
  id?: number;
  name: string;
  icon: string;
  order: number;
  type: CategoryType;
  subCategories?: Category[];
}

export enum CategoryType {
  EXPENSE,
  INCOME,
}
