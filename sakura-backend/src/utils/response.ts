import { Response } from 'express';

export const successResponse = (
    res: Response,
    data: any,
    message: string = 'Success',
    statusCode: number = 200
) => {
    return res.status(statusCode).json({
        success: true,
        message,
        data,
    });
};

export const errorResponse = (
    res: Response,
    message: string = 'Error',
    statusCode: number = 400,
    errors?: any
) => {
    return res.status(statusCode).json({
        success: false,
        message,
        errors,
    });
};

export const paginatedResponse = (
    res: Response,
    data: any[],
    page: number,
    limit: number,
    total: number,
    message: string = 'Success'
) => {
    return res.status(200).json({
        success: true,
        message,
        data,
        pagination: {
            page,
            limit,
            total,
            totalPages: Math.ceil(total / limit),
            hasNext: page * limit < total,
            hasPrev: page > 1,
        },
    });
};
