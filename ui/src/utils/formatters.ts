export const formatClassName = (fullClassName: string): string => {
    if (!fullClassName) return '';

    const parts = fullClassName.split('.');
    if (parts.length <= 2) return fullClassName;

    // Keep the last 2 parts (package + class)
    const lastTwo = parts.slice(-2);
    // Take the first letter of the preceding parts
    const prefix = parts.slice(0, -2).map(p => p[0]).join('.');

    return `${prefix}.${lastTwo.join('.')}`;
};
